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
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
                for (i1 in 0 until field.totalParticles) {
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
        return fields[p.xField][p.yField]
    }

    fun logic() {
        eachParticleDo {
            it.adjustPositionBasedOnVelocity()
            it.velocity.slowDown()
            it.velocity.normalize()
            it.detectBorders()
        }

        processLinks()
        moveParticlesThroughFields()

        // dividing scene into parts to reduce complexity
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
                for (i1 in 0 until field.totalParticles) {
                    val a = field.particleByIndex(i1)
                    var particleToLink: Particle? = null
                    var particleToLinkMinDist2 = ((w + h) * (w + h)).toFloat()
                    for (j1 in i1 + 1 until field.totalParticles) {
                        val b = field.particleByIndex(j1)
                        val d2 = a applyForceTo b
                        if (d2 != -1f && d2 < particleToLinkMinDist2) {
                            particleToLinkMinDist2 = d2
                            particleToLink = b
                        }
                    }
                    if (i < fw - 1) {
                        val iNext = i + 1
                        val field1 = fields[iNext][j]
                        for (j1 in 0 until field1.totalParticles) {
                            val b = field1.particleByIndex(j1)
                            val d2 = a applyForceTo b
                            if (d2 != -1f && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2
                                particleToLink = b
                            }
                        }
                    }
                    if (j < fh - 1) {
                        val jNext = j + 1
                        val field1 = fields[i][jNext]
                        for (j1 in 0 until field1.totalParticles) {
                            val b = field1.particleByIndex(j1)
                            val d2 = a applyForceTo b
                            if (d2 != -1f && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2
                                particleToLink = b
                            }
                        }
                        if (i < fw - 1) {
                            val iNext = i + 1
                            val field2 = fields[iNext][jNext]
                            for (j1 in 0 until field2.totalParticles) {
                                val b = field2.particleByIndex(j1)
                                val d2 = a applyForceTo b
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

    private fun processLinks() {
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
}
