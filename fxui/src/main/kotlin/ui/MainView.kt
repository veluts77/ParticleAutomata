package ui

import javafx.scene.canvas.Canvas
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import tornadofx.*

class MainView : View() {

    val writableImage = WritableImage(100, 100)
    var cnv: Canvas = Canvas(1.0, 1.0)

    override val root = pane {
        setMinSize(500.0, 500.0)
        title = "Particle Automata"

        group {
            rectangle {
                x = 25.0
                y = 25.0
                width = 250.0
                height = 250.0
                fill = Color.BEIGE
            }

            imageview {
                x = 50.0
                y = 50.0
                image = writableImage

            }

            cnv = canvas {
                width = 100.0
                height = 100.0
                layoutX = 100.0
                layoutY = 100.0
            }
        }

        runAsync {
//            while (true) {
                writableImage.pixelWriter.setColor(
                        randomX(),
                        randomY(),
                        Color.BLACK
                )

                with(cnv.graphicsContext2D) {
                    fill = Color.AZURE
                    fillRect(0.0, 0.0, 99.0, 99.0)
                    rect(0.0, 0.0, 99.0, 99.0)
                    fill()
                }

                try {
                    Thread.sleep(15)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
//        }
    }

    private fun randomY() = Math.round(Math.random() * (writableImage.height - 1)).toInt()

    private fun randomX() = Math.round(Math.random() * (writableImage.width - 1)).toInt()
}