package particle.kbetter


import java.util.ArrayList

class Logic(
        private val fields: Array<Array<Field?>>,
        private val force: Force
) {
    val links = ArrayList<Link>()

    fun logic() {
        for (i in 0 until Settings.fw) {
            for (j in 0 until Settings.fh) {
                val field = fields[i][j]!!
                for (i1 in field.particles.indices) {
                    val a = field.particles[i1]
                    a.x += a.sx
                    a.y += a.sy
                    a.sx *= 0.98f
                    a.sy *= 0.98f
                    // velocity normalization
                    // idk if it is still necessary
                    val magnitude = Math.sqrt((a.sx * a.sx + a.sy * a.sy).toDouble()).toFloat()
                    if (magnitude > 1f) {
                        a.sx /= magnitude
                        a.sy /= magnitude
                    }
                    // border repulsion
                    if (a.x < Settings.BORDER) {
                        a.sx += Settings.SPEED * 0.05f
                        if (a.x < 0) {
                            a.x = -a.x
                            a.sx *= -0.5f
                        }
                    } else if (a.x > Settings.w - Settings.BORDER) {
                        a.sx -= Settings.SPEED * 0.05f
                        if (a.x > Settings.w) {
                            a.x = Settings.w * 2 - a.x
                            a.sx *= -0.5f
                        }
                    }
                    if (a.y < Settings.BORDER) {
                        a.sy += Settings.SPEED * 0.05f
                        if (a.y < 0) {
                            a.y = -a.y
                            a.sy *= -0.5f
                        }
                    } else if (a.y > Settings.h - Settings.BORDER) {
                        a.sy -= Settings.SPEED * 0.05f
                        if (a.y > Settings.h) {
                            a.y = Settings.h * 2 - a.y
                            a.sy *= -0.5f
                        }
                    }
                }
            }
        }
        run {
            var i = 0
            while (i < links.size) {
                val link = links[i]
                val a = link.a
                val b = link.b
                val d2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)
                if (d2 > Settings.MAX_DIST2 / 4f) {
                    a.links--
                    b.links--
                    a.bonds.minus(b)
                    b.bonds.minus(a)
                    links.remove(link)
                    i--
                } else {
                    if (d2 > Settings.NODE_RADIUS * Settings.NODE_RADIUS * 4) {
                        val angle = Math.atan2((a.y - b.y).toDouble(), (a.x - b.x).toDouble())
                        a.sx += Math.cos(angle).toFloat() * Settings.LINK_FORCE * Settings.SPEED
                        a.sy += Math.sin(angle).toFloat() * Settings.LINK_FORCE * Settings.SPEED
                        b.sx -= Math.cos(angle).toFloat() * Settings.LINK_FORCE * Settings.SPEED
                        b.sy -= Math.sin(angle).toFloat() * Settings.LINK_FORCE * Settings.SPEED
                    }
                }
                i++
            }
        }
        // moving particle to another field
        for (i in 0 until Settings.fw) {
            for (j in 0 until Settings.fh) {
                val field = fields[i][j]!!
                val toRemoveParticles = mutableListOf<Particle>()
                for (i1 in field.particles.indices) {
                    val a = field.particles[i1]
                    if ((a.x / Settings.MAX_DIST).toInt() != i || (a.y / Settings.MAX_DIST).toInt() != j) {
                        toRemoveParticles.add(a)
                        fields[(a.x / Settings.MAX_DIST).toInt()][(a.y / Settings.MAX_DIST).toInt()]!!.particles.add(a)
                    }
                }
                field.particles.removeAll(toRemoveParticles)
            }
        }
        // dividing scene into parts to reduce complexity
        for (i in 0 until Settings.fw) {
            for (j in 0 until Settings.fh) {
                val field = fields[i][j]!!
                for (i1 in field.particles.indices) {
                    val a = field.particles[i1]
                    var particleToLink: Particle? = null
                    var particleToLinkMinDist2 = ((Settings.w + Settings.h) * (Settings.w + Settings.h)).toFloat()
                    for (j1 in i1 + 1 until field.particles.size) {
                        val b = field.particles[j1]
                        val d2 = force.applyForce(a, b)
                        if (d2 != -1f && d2 < particleToLinkMinDist2) {
                            particleToLinkMinDist2 = d2
                            particleToLink = b
                        }
                    }
                    if (i < Settings.fw - 1) {
                        val iNext = i + 1
                        val field1 = fields[iNext][j]!!
                        for (j1 in field1.particles.indices) {
                            val b = field1.particles[j1]
                            val d2 = force.applyForce(a, b)
                            if (d2 != -1f && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2
                                particleToLink = b
                            }
                        }
                    }
                    if (j < Settings.fh - 1) {
                        val jNext = j + 1
                        val field1 = fields[i][jNext]!!
                        for (j1 in field1.particles.indices) {
                            val b = field1.particles[j1]
                            val d2 = force.applyForce(a, b)
                            if (d2 != -1f && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2
                                particleToLink = b
                            }
                        }
                        if (i < Settings.fw - 1) {
                            val iNext = i + 1
                            val field2 = fields[iNext][jNext]!!
                            for (j1 in field2.particles.indices) {
                                val b = field2.particles[j1]
                                val d2 = force.applyForce(a, b)
                                if (d2 != -1f && d2 < particleToLinkMinDist2) {
                                    particleToLinkMinDist2 = d2
                                    particleToLink = b
                                }
                            }
                        }
                    }
                    if (particleToLink != null) {
                        a.bonds.plus(particleToLink)
                        particleToLink.bonds.plus(a)
                        a.links++
                        particleToLink.links++
                        links.add(Link(a, particleToLink))
                    }
                }
            }
        }
    }

}
