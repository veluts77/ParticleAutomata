package ui

import tornadofx.App
import tornadofx.launch

class ParticleApp : App(MainView::class)

fun launchApp() {
    launch<ParticleApp>()
}