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
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
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
                    // processing the rest of particles in current field
                    for (j1 in i1 + 1 until field.totalParticles) {
                        tryUpdateParticleToLink(field.particleByIndex(j1))
                    }
                    if (i < fw - 1) { // checking the field at right
                        fields[i + 1][j].eachParticleDo {
                            tryUpdateParticleToLink(it)
                        }
                    }
                    if (j < fh - 1) { // checking the filed at bottom
                        fields[i][j + 1].eachParticleDo {
                            tryUpdateParticleToLink(it)
                        }
                        if (i < fw - 1) { // checking the field at bottom right
                            fields[i + 1][j + 1].eachParticleDo {
                                tryUpdateParticleToLink(it)
                            }
                        }
                    }
                    if (particleToLink != null) {
                        links.add(Link(a, particleToLink!!))
                    }
                }
            }
        }
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
