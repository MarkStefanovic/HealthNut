package src.app

import javafx.scene.Scene
import javafx.scene.image.Image
import tornadofx.*

class HealthNutApp : App(HealthNutWorkspace::class, Styles::class) {
    override fun createPrimaryScene(view: UIComponent): Scene = Scene(view.root, 1200.0, 600.0)

    init {
        addStageIcon(Image("app-icon.png"))
    }
}

