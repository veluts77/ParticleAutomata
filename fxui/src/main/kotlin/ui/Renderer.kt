package ui

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import particle.ParticlesScene
import particle.Settings

class Renderer(val graphicsContext: GraphicsContext) : Runnable {

    private val scene = ParticlesScene()

    init {
        scene.addRandomParticles()
    }

    override fun run() {
        while (true) {
            render(graphicsContext)
            try {
                Thread.sleep(15)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun render(gc: GraphicsContext) {
        val beginTime = System.currentTimeMillis()

        drawScene(gc)
        processLogic()

        val endTime = System.currentTimeMillis()
        showTime(gc, endTime - beginTime)
    }

    private fun drawScene(gc: GraphicsContext) {
        val beginTime = System.currentTimeMillis()

        drawBackground(gc)
        drawParticles(gc)
        drawLinks(gc)

        val endTime = System.currentTimeMillis()
        showInnerTime(gc, endTime - beginTime)
    }

    private fun drawBackground(gc: GraphicsContext) {
        gc.fill = Color.rgb(20, 55, 75)
        gc.fillRect(
                0.0,
                0.0,
                Settings.w.toDouble(),
                Settings.h.toDouble())
    }

    private fun drawParticles(gc: GraphicsContext) {
        scene.eachParticleDo {
            gc.fill = Color.rgb(it.color.red, it.color.green, it.color.blue)
            gc.fillOval(
                    (it.screenX - Settings.NODE_RADIUS).toDouble(),
                    (it.screenY - Settings.NODE_RADIUS).toDouble(),
                    (Settings.NODE_RADIUS * 2).toDouble(),
                    (Settings.NODE_RADIUS * 2).toDouble()
            )
        }
    }

    private fun drawLinks(gc: GraphicsContext) {
        gc.stroke = Color.BEIGE
        scene.eachLinkDo {
            gc.strokeLine(
                    it.screenX1.toDouble(),
                    it.screenY1.toDouble(),
                    it.screenX2.toDouble(),
                    it.screenY2.toDouble()
            )
        }
    }

    private fun processLogic() {
        for (i in 0 until Settings.SKIP_FRAMES) scene.logic()
    }

    private fun showTime(gc: GraphicsContext, time: Long) {
        gc.fill = Color.YELLOW
        gc.fillText(time.toString(), 30.0, 62.0)
    }

    private fun showInnerTime(gc: GraphicsContext, time: Long) {
        gc.fill = Color.WHITE
        gc.fillText(time.toString(), 25.0, 45.0)
    }
}