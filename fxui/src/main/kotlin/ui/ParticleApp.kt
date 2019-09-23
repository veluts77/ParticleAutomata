package ui

import javafx.scene.Group
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import javafx.scene.Scene
import java.awt.Color
import com.sun.javafx.robot.impl.FXRobotHelper.getChildren
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle


class ParticleApp : App(MainView::class) {
    override fun start(stage: Stage) {
        //super.start(stage)
        val root = Group()

        val scene = Scene(root, 500.0, 500.0, true)

        val r = Rectangle(25.0, 25.0, 250.0, 250.0)
        r.setFill(Paint.valueOf("blue"))
        root.children.add(r)

        stage.title = "JavaFX Scene Graph Demo"
        stage.scene = scene
        stage.show()
    }
}

fun launchApp() {
    launch<ParticleApp>()
}