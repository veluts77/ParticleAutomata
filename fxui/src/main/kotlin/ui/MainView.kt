package ui

import tornadofx.View
import tornadofx.label
import tornadofx.vbox

class MainView : View() {
    override val root = vbox {
        label("Hello World")
    }
}