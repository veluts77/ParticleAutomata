package particle.raw

import javax.swing.*
import java.awt.*
import java.awt.image.BufferedImage
import java.util.ArrayList

class Form : JFrame(), Runnable {

    private val w = 1000
    private val h = 800

    private val BG = Color(20, 55, 75, 255)
    private val LINK = Color(255, 230, 0, 100)

    private val NODE_RADIUS = 5
    private val NODE_COUNT = 800
    private val MAX_DIST = 100
    private val MAX_DIST2 = MAX_DIST * MAX_DIST
    private val SPEED = 4f
    private val SKIP_FRAMES = 1
    private val BORDER = 30

    private val fw = w / MAX_DIST + 1
    private val fh = h / MAX_DIST + 1

    private val links = ArrayList<Link>()
    private val LINK_FORCE = -0.015f
    private var frame = 0

    private val img = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)

    // array for dividing scene into parts to reduce complexity
    private val fields = Array<Array<Field?>>(fw) { arrayOfNulls(fh) }

    init {
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                fields[i][j] = Field()
            }
        }
        // put particles randomly
        for (i in 0 until NODE_COUNT) {
            add((Math.random() * COUPLING.size).toInt(), (Math.random() * w).toFloat(), (Math.random() * h).toFloat())
        }

        this.setSize(w + 16, h + 38)
        this.isVisible = true
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.setLocation(50, 50)
        this.add(JLabel(ImageIcon(img)))
    }

    private fun add(type: Int, x: Float, y: Float): Particle {
        val p = Particle(ParticleType.values()[type], x, y)
        fields[(p.x / MAX_DIST).toInt()][(p.y / MAX_DIST).toInt()]!!.particles.add(p)
        return p
    }

    override fun run() {
        while (true) {
            this.repaint()
        }
    }

    override fun paint(g: Graphics?) {
        val beginTime = System.currentTimeMillis()
        drawScene(img)
        for (i in 0 until SKIP_FRAMES) logic()
        (g as Graphics2D).drawImage(img, null, 8, 30)
        frame++
        val endTime = System.currentTimeMillis()
        val time = (endTime - beginTime).toString()
        g.color = Color.green
        g.drawString(time, 30, 42)
    }

    private fun drawScene(image: BufferedImage) {
        val beginTime = System.currentTimeMillis()
        val g2 = image.createGraphics()
        g2.color = BG
        g2.fillRect(0, 0, w, h)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
                for (i1 in field!!.particles.indices) {
                    val a = field.particles[i1]
                    g2.color = a.color
                    g2.fillOval(a.x.toInt() - NODE_RADIUS, a.y.toInt() - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2)
                }
            }
        }
        g2.color = LINK
        for (link in links) {
            g2.drawLine(link.a.x.toInt(), link.a.y.toInt(), link.b.x.toInt(), link.b.y.toInt())
        }
        val endTime = System.currentTimeMillis()
        val time = (endTime - beginTime).toString()
        g2.color = Color.white
        g2.drawString(time, 25, 25)
    }

    private fun logic() {
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
                for (i1 in field!!.particles.indices) {
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
                    if (a.x < BORDER) {
                        a.sx += SPEED * 0.05f
                        if (a.x < 0) {
                            a.x = -a.x
                            a.sx *= -0.5f
                        }
                    } else if (a.x > w - BORDER) {
                        a.sx -= SPEED * 0.05f
                        if (a.x > w) {
                            a.x = w * 2 - a.x
                            a.sx *= -0.5f
                        }
                    }
                    if (a.y < BORDER) {
                        a.sy += SPEED * 0.05f
                        if (a.y < 0) {
                            a.y = -a.y
                            a.sy *= -0.5f
                        }
                    } else if (a.y > h - BORDER) {
                        a.sy -= SPEED * 0.05f
                        if (a.y > h) {
                            a.y = h * 2 - a.y
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
                if (d2 > MAX_DIST2 / 4) {
                    a.links--
                    b.links--
                    a.bonds.minus(b)  // remove(b)
                    b.bonds.minus(a)
                    links.remove(link)
                    i--
                } else {
                    if (d2 > NODE_RADIUS * NODE_RADIUS * 4) {
                        val angle = Math.atan2((a.y - b.y).toDouble(), (a.x - b.x).toDouble())
                        a.sx += Math.cos(angle).toFloat() * LINK_FORCE * SPEED
                        a.sy += Math.sin(angle).toFloat() * LINK_FORCE * SPEED
                        b.sx -= Math.cos(angle).toFloat() * LINK_FORCE * SPEED
                        b.sy -= Math.sin(angle).toFloat() * LINK_FORCE * SPEED
                    }
                }
                i++
            }
        }
        // moving particle to another field
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
                val toRemoveParticles = mutableListOf<Particle>()
                for (i1 in field!!.particles.indices) {
                    val a = field.particles[i1]
                    if ((a.x / MAX_DIST).toInt() != i || (a.y / MAX_DIST).toInt() != j) {
                        toRemoveParticles.add(a)
                        fields[(a.x / MAX_DIST).toInt()][(a.y / MAX_DIST).toInt()]!!.particles.add(a)
                    }
                }
                field.particles.removeAll(toRemoveParticles)
            }
        }
        // dividing scene into parts to reduce complexity
        for (i in 0 until fw) {
            for (j in 0 until fh) {
                val field = fields[i][j]
                for (i1 in field!!.particles.indices) {
                    val a = field.particles[i1]
                    var particleToLink: Particle? = null
                    var particleToLinkMinDist2 = ((w + h) * (w + h)).toFloat()
                    for (j1 in i1 + 1 until field.particles.size) {
                        val b = field.particles[j1]
                        val d2 = applyForce(a, b)
                        if (d2 != -1f && d2 < particleToLinkMinDist2) {
                            particleToLinkMinDist2 = d2
                            particleToLink = b
                        }
                    }
                    if (i < fw - 1) {
                        val iNext = i + 1
                        val field1 = fields[iNext][j]
                        for (j1 in field1!!.particles.indices) {
                            val b = field1.particles[j1]
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
                        for (j1 in field1!!.particles.indices) {
                            val b = field1.particles[j1]
                            val d2 = applyForce(a, b)
                            if (d2 != -1f && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2
                                particleToLink = b
                            }
                        }
                        if (i < fw - 1) {
                            val iNext = i + 1
                            val field2 = fields[iNext][jNext]
                            for (j1 in field2!!.particles.indices) {
                                val b = field2.particles[j1]
                                val d2 = applyForce(a, b)
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

    private fun applyForce(a: Particle, b: Particle): Float {
        var d2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)
        var canLink = false
        if (d2 < MAX_DIST2) {
            var dA = COUPLING[a.getType()][b.getType()] / d2
            var dB = COUPLING[b.getType()][a.getType()] / d2
            if (a.links < LINKS[a.getType()] && b.links < LINKS[b.getType()]) {
                if (d2 < MAX_DIST2 / 4) {
                    if (!a.bonds.contains(b) && !b.bonds.contains(a)) {
                        var typeCountA = 0
                        for (p in a.bonds) {
                            if (p.getType() == b.getType()) typeCountA++
                        }
                        var typeCountB = 0
                        for (p in b.bonds) {
                            if (p.getType() == a.getType()) typeCountB++
                        }
                        if (typeCountA < LINKS_POSSIBLE[a.getType()][b.getType()] && typeCountB < LINKS_POSSIBLE[b.getType()][a.getType()]) {
                            canLink = true
                        }
                    }
                }
            } else {
                if (!a.bonds.contains(b) && !b.bonds.contains(a)) {
                    dA = 1 / d2
                    dB = 1 / d2
                }
            }
            val angle = Math.atan2((a.y - b.y).toDouble(), (a.x - b.x).toDouble())
            if (d2 < 1) d2 = 1f
            if (d2 < NODE_RADIUS * NODE_RADIUS * 4) {
                dA = 1 / d2
                dB = 1 / d2
            }
            a.sx += Math.cos(angle).toFloat() * dA * SPEED
            a.sy += Math.sin(angle).toFloat() * dA * SPEED
            b.sx -= Math.cos(angle).toFloat() * dB * SPEED
            b.sy -= Math.sin(angle).toFloat() * dB * SPEED
        }
        return if (canLink) d2 else -1f
    }

    companion object {

        private val COUPLING = arrayOf(
                floatArrayOf(1f, 1f, -1f),
                floatArrayOf(1f, 1f, 1f),
                floatArrayOf(1f, 1f, 1f))

        private val LINKS = intArrayOf(1, 3, 2)

        private val LINKS_POSSIBLE = arrayOf(
                floatArrayOf(0f, 1f, 1f),
                floatArrayOf(1f, 2f, 1f),
                floatArrayOf(1f, 1f, 2f))
    }

}