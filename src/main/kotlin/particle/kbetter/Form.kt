package particle.kbetter


import javax.swing.*
import java.awt.*
import java.awt.image.BufferedImage
//import particle.jbetter.Logic
//import particle.jbetter.Force
//import particle.jbetter.Field
//import particle.jbetter.Particle
//import particle.jbetter.ParticleType

class Form : JFrame(), Runnable {

    private var frame = 0

    private val img = BufferedImage(
            Settings.w,
            Settings.h,
            BufferedImage.TYPE_INT_RGB)

    // array for dividing scene into parts to reduce complexity
    private val fields = Array<Array<Field?>>(Settings.fw) { arrayOfNulls(Settings.fh) }
    private val logic: Logic
    private val force: Force

    init {
        this.force = Force()

        for (i in 0 until Settings.fw) {
            for (j in 0 until Settings.fh) {
                fields[i][j] = Field()
            }
        }
        // put particles randomly
        for (i in 0 until Settings.NODE_COUNT) {
            add(
                    (Math.random() * force.couplingLength()).toInt(),
                    (Math.random() * Settings.w).toFloat(),
                    (Math.random() * Settings.h).toFloat()
            )
        }

        this.setSize(Settings.w + 16, Settings.h + 38)
        this.isVisible = true
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.setLocation(50, 50)
        this.add(JLabel(ImageIcon(img)))
        this.logic = Logic(fields, force)
    }

    private fun add(type: Int, x: Float, y: Float): Particle {
        val p = Particle(ParticleType.values()[type], x, y)
        fields[(p.x / Settings.MAX_DIST).toInt()][(p.y / Settings.MAX_DIST).toInt()]!!.particles.add(p)
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
        for (i in 0 until Settings.SKIP_FRAMES) logic.logic()
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
        g2.color = Settings.BG
        g2.fillRect(0, 0, Settings.w, Settings.h)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        for (i in 0 until Settings.fw) {
            for (j in 0 until Settings.fh) {
                val field = fields[i][j]!!
                for (i1 in field.particles.indices) {
                    val a = field.particles[i1]
                    g2.color = a.color
                    g2.fillOval(
                            a.x.toInt() - Settings.NODE_RADIUS,
                            a.y.toInt() - Settings.NODE_RADIUS,
                            Settings.NODE_RADIUS * 2,
                            Settings.NODE_RADIUS * 2)
                }
            }
        }
        g2.color = Settings.LINK
        for (link in logic.links) {
            g2.drawLine(link.a.x.toInt(), link.a.y.toInt(), link.b.x.toInt(), link.b.y.toInt())
        }
        val endTime = System.currentTimeMillis()
        val time = (endTime - beginTime).toString()
        g2.color = Color.white
        g2.drawString(time, 25, 25)
    }
}