package particle

import particle.Settings.COUPLING
import particle.Settings.MAX_DIST
import particle.Settings.NODE_COUNT
import particle.Settings.NODE_RADIUS
import particle.Settings.h
import particle.Settings.w

internal class ParticlesScene {

    private val fw = w / MAX_DIST + 1
    private val fh = h / MAX_DIST + 1
    private val squaredMaxDist = MAX_DIST * MAX_DIST

    private val fields = Array(fw) { Array(fh) { Field() } }
    private val links: MutableList<Link> = mutableListOf()

    fun addRandomParticles() {
        for (i in 0 until NODE_COUNT) {
            addOneParticle((Math.random() * COUPLING.size).toInt(),
                    (Math.random() * w).toFloat(),
                    (Math.random() * h).toFloat())
        }
    }

    private fun addOneParticle(type: Int, x: Float, y: Float) {
        val p = Particle(ParticleType.values()[type], Point(x, y))
        fieldFor(p).add(p)
    }

    fun eachParticleDo(consumer: (Particle) -> Unit) {
        fields.forEach { row ->
            row.forEach { field ->
                field.eachParticleDo(consumer)
            }
        }
    }

    fun eachLinkDo(consumer: (Link) -> Unit) {
        links.forEach(consumer)
    }

    fun logic() {
        eachParticleDo {
            it.adjustPositionBasedOnVelocity()
            it.velocity.slowDown()
            it.velocity.normalize()
            it.detectBorders()
        }

        processExistingLinks()
        moveParticlesThroughFields()
        addNewLinks()
    }

    private fun addNewLinks() {
        eachFieldDo { field ->
            for (i1 in 0 until field.totalParticles) {
                val a = field.particleByIndex(i1)
                var particleToLink: Particle? = null
                var particleToLinkMinDist2 = ((w + h) * (w + h)).toFloat()

                fun tryUpdateParticleToLink(b: Particle) {
                    val d2 = a applyForceTo b
                    if (d2 != -1f && d2 < particleToLinkMinDist2) {
                        particleToLinkMinDist2 = d2
                        particleToLink = b
                    }
                }

                for (i2 in i1 + 1 until field.totalParticles) {
                    tryUpdateParticleToLink(field.particleByIndex(i2))
                }

                setOf(
                        field.fieldAtRight(),
                        field.fieldAtBottom(),
                        field.fieldAtBottomRight()
                ).forEach { f ->
                    f.eachParticleDo {
                        tryUpdateParticleToLink(it)
                    }
                }

                if (particleToLink != null) {
                    links.add(Link(a, particleToLink!!))
                }
            }
        }
    }

    private fun eachFieldDo(consumer: (Field) -> Unit) {
        fields.forEach { row ->
            row.forEach {
                consumer.invoke(it)
            }
        }
    }

    private fun Field.fieldAtRight(): Field {
        val columnAndRow = this.columnAndRow()
        if (columnAndRow[0] == -1) return Field()
        val i = columnAndRow[0]
        if (i == fw) return Field()
        val j = columnAndRow[1]
        return fields[i + 1][j]
    }

    private fun Field.fieldAtBottom(): Field {
        val columnAndRow = this.columnAndRow()
        if (columnAndRow[0] == -1) return Field()
        val j = columnAndRow[1]
        if (j == fh) return Field()
        val i = columnAndRow[0]
        return fields[i][j + 1]
    }

    private fun Field.fieldAtBottomRight(): Field {
        val columnAndRow = this.columnAndRow()
        if (columnAndRow[0] == -1) return Field()
        val i = columnAndRow[0]
        if (i == fw) return Field()
        val j = columnAndRow[1]
        if (j == fh) return Field()
        return fields[i + 1][j + 1]
    }

    private fun Field.columnAndRow(): Array<Int> {
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                if (fields[i][j] == this) return arrayOf(i, j)
            }
        }
        return arrayOf(-1, -1)
    }

    private fun moveParticlesThroughFields() {
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
                val particlesToRemove = mutableListOf<Particle>()
                for (i1 in 0 until field.totalParticles) {
                    val p = field.particleByIndex(i1)
                    if (p.xField != i || p.yField != j) {
                        particlesToRemove.add(p)
                        fieldFor(p).add(p)
                    }
                }
                field.removeAll(particlesToRemove)
            }
        }
    }

    private fun processExistingLinks() {
        val linksToRemove = mutableListOf<Link>()
        eachLinkDo {
            val d2 = it.squaredDistance
            if (d2 > squaredMaxDist / 4f) {
                it.unlink()
                linksToRemove.add(it)
            } else if (d2 > NODE_RADIUS * NODE_RADIUS * 4) {
                it.adjustParticlesVelocity()
            }
        }
        links.removeAll(linksToRemove)
    }

    private fun fieldFor(p: Particle): Field {
        return fields[p.xField][p.yField]
    }
}
