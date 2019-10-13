package ui

import javafx.scene.canvas.Canvas
import particle.Settings
import tornadofx.View
import tornadofx.canvas
import tornadofx.group
import tornadofx.pane

class MainView : View() {

    private var cnv: Canvas = Canvas(1.0, 1.0)

    override val root = pane {
        setMinSize(500.0, 500.0)
        title = "Particle Automata"

        group {
            cnv = canvas {
                width = Settings.w.toDouble()
                height = Settings.h.toDouble()
                layoutX = 0.0
                layoutY = 0.0
            }

            Thread(Renderer(cnv.graphicsContext2D)).start()
        }
    }
}