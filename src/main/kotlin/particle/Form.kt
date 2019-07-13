package particle

import particle.Settings.NODE_RADIUS
import particle.Settings.SKIP_FRAMES
import particle.Settings.h
import particle.Settings.w
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

class Form : JFrame(), Runnable {

    private val BG = Color(20, 55, 75, 255)
    private val LINK = Color(255, 230, 0, 100)

    private val img = BufferedImage(
            w, h, BufferedImage.TYPE_INT_RGB)

    private val fields = Fields()

    init {
        fields.addRandomParticles()

        this.setSize(w + 16, h + 38)
        this.isVisible = true
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.setLocation(50, 50)
        this.add(JLabel(ImageIcon(img)))
    }


    override fun run() {
        while (true) {
            repaint()
            try {
                Thread.sleep(15)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    override fun paint(g: Graphics?) {
        val beginTime = System.currentTimeMillis()

        drawScene(img)
        process_logic()
        (g as Graphics2D).drawImage(img, null, 8, 30)

        val endTime = System.currentTimeMillis()
        showTime(g, endTime - beginTime)
    }

    private fun drawScene(image: BufferedImage) {
        val beginTime = System.currentTimeMillis()

        val g2 = image.createGraphics()
        g2.color = BG
        g2.fillRect(0, 0, w, h)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        fields.eachParticleDo {
            g2.color = it.color
            g2.fillOval(
                    it.screenX - NODE_RADIUS,
                    it.screenY - NODE_RADIUS,
                    NODE_RADIUS * 2,
                    NODE_RADIUS * 2
            )
        }

        g2.color = LINK
        fields.eachLinkDo {
            g2.drawLine(
                    it.screenX1(),
                    it.screenY1(),
                    it.screenX2(),
                    it.screenY2()
            )
        }

        val endTime = System.currentTimeMillis()
        showInnerTime(g2, endTime - beginTime)
    }

    private fun process_logic() {
        for (i in 0 until SKIP_FRAMES) {
            fields.logic()
        }
    }

    private fun showTime(g: Graphics, time: Long) {
        g.color = Color.yellow
        g.drawString(time.toString(), 30, 62)
    }

    private fun showInnerTime(g: Graphics, time: Long) {
        g.color = Color.white
        g.drawString(time.toString(), 25, 45)
    }
}
