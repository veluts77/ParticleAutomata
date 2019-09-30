package ui

import javafx.scene.paint.Color
import tornadofx.*

class MainView : View() {
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
        }
    }
}