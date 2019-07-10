package particle.kobjects


import particle.kobjects.Settings.COUPLING
import particle.kobjects.Settings.MAX_DIST
import particle.kobjects.Settings.NODE_COUNT
import particle.kobjects.Settings.NODE_RADIUS
import particle.kobjects.Settings.h
import particle.kobjects.Settings.w

internal class Fields {

    private val fw = w / MAX_DIST + 1
    private val fh = h / MAX_DIST + 1
    private val MAX_DIST2 = MAX_DIST * MAX_DIST

    // array for dividing scene into parts to reduce complexity
    private val fields = Array(fw) { Array<Field>(fh) { Field() } }

    private val links: MutableList<Link> = mutableListOf()

    fun addRandomParticles() {
        for (i in 0 until NODE_COUNT) {
            addOneParticle((Math.random() * COUPLING.size).toInt(),
                    (Math.random() * w).toFloat(),
                    (Math.random() * h).toFloat())
        }
    }

    private fun addOneParticle(type: Int, x: Float, y: Float) {
        val p = Particle(ParticleType.values()[type], x, y)
        fieldFor(p).add(p)
    }

    fun eachParticleDo(consumer: (Particle) -> Unit) {
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
                for (i1 in 0 until field.totalParticles()) {
                    val p = field.particleByIndex(i1)
                    consumer.invoke(p)
                }
            }
        }
    }

    fun eachLinkDo(consumer: (Link) -> Unit) {
        for (link in links) {
            consumer.invoke(link)
        }
    }

    private fun fieldFor(p: Particle): Field {
        return fields[p.xField()][p.yField()]
    }

    fun logic() {
        eachParticleDo {
            it.adjustPosition()
            it.slowDownVelocity()
            it.normalizeVelocity()
        }

        run {
            var i = 0
            while (i < links.size) {
                val link = links[i]
                val d2 = link.squaredDistance()
                if (d2 > MAX_DIST2 / 4f) {
                    link.unlink()
                    links.remove(link)
                    i--
                } else if (d2 > NODE_RADIUS * NODE_RADIUS * 4) {
                    link.adjustParticlesVelocity()
                }
                i++
            }
        }
        // moving particle to another field
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
                val toRemoveParticles = mutableListOf<Particle>()
                for (i1 in 0 until field.totalParticles()) {
                    val p = field.particleByIndex(i1)
                    if (p.xField() != i || p.yField() != j) {
                        //field.remove(p)
                        toRemoveParticles.add(p)
                        fieldFor(p).add(p)
                    }
                }
                field.removeAll(toRemoveParticles)
            }
        }
        // dividing scene into parts to reduce complexity
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
                for (i1 in 0 until field.totalParticles()) {
                    val a = field.particleByIndex(i1)
                    var particleToLink: Particle? = null
                    var particleToLinkMinDist2 = ((w + h) * (w + h)).toFloat()
                    for (j1 in i1 + 1 until field.totalParticles()) {
                        val b = field.particleByIndex(j1)
                        val d2 = applyForce(a, b)
                        if (d2 != -1f && d2 < particleToLinkMinDist2) {
                            particleToLinkMinDist2 = d2
                            particleToLink = b
                        }
                    }
                    if (i < fw - 1) {
                        val iNext = i + 1
                        val field1 = fields[iNext][j]
                        for (j1 in 0 until field1.totalParticles()) {
                            val b = field1.particleByIndex(j1)
                            val d2 = applyForce(a, b)
                            if (d2 != -1f && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2
                                particleToLink = b
                            }
                        }
                    }
                    if (j < fh - 1) {
                        val jNext = j + 1
                        val field1 = fields[i][jNext]
                        for (j1 in 0 until field1.totalParticles()) {
                            val b = field1.particleByIndex(j1)
                            val d2 = applyForce(a, b)
                            if (d2 != -1f && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2
                                particleToLink = b
                            }
                        }
                        if (i < fw - 1) {
                            val iNext = i + 1
                            val field2 = fields[iNext][jNext]
                            for (j1 in 0 until field2.totalParticles()) {
                                val b = field2.particleByIndex(j1)
                                val d2 = applyForce(a, b)
                                if (d2 != -1f && d2 < particleToLinkMinDist2) {
                                    particleToLinkMinDist2 = d2
                                    particleToLink = b
                                }
                            }
                        }
                    }
                    if (particleToLink != null) {
                        links.add(Link(a, particleToLink))
                    }
                }
            }
        }
    }

    private fun applyForce(a: Particle, b: Particle): Float {
        var d2 = a.squaredDistanceTo(b)
        var canLink = false
        if (d2 < MAX_DIST2) {
            var dA = a.couplingWith(b) / d2
            var dB = b.couplingWith(a) / d2
            if (a.freeLinksAvailable() && b.freeLinksAvailable()) {
                canLink = d2 < MAX_DIST2 / 4f &&
                        notYetLinked(a, b) &&
                        a.mayLinkTo(b)
            } else {
                if (notYetLinked(a, b)) {
                    dA = 1 / d2
                    dB = 1 / d2
                }
            }
            val angle = a.angleTo(b)
            if (d2 < 1) d2 = 1f
            if (d2 < NODE_RADIUS * NODE_RADIUS * 4) {
                dA = 1 / d2
                dB = 1 / d2
            }
            a.addVelocityToPositiveDirection(angle, dA)
            b.addVelocityToNegativeDirection(angle, dB)
        }
        return if (canLink) d2 else -1f
    }

    private fun notYetLinked(a: Particle, b: Particle): Boolean {
        return a.isNotLinkedTo(b) && b.isNotLinkedTo(a)
    }
}