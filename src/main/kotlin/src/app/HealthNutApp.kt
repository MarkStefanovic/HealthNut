package src.app

import javafx.scene.Scene
import javafx.scene.control.TabPane
import javafx.scene.image.Image
import org.jetbrains.exposed.sql.Database
import src.controller.*
import src.view.FoodListView
import src.view.diary.DiaryEntryMainView
import tornadofx.*

class HealthNutApp : App(HealthNutWorkspace::class, Styles::class) {
    override fun createPrimaryScene(view: UIComponent): Scene = Scene(view.root, 1000.0, 600.0)

    init {
        addStageIcon(Image("app-icon.png"))
    }
}

class HealthNutWorkspace : Workspace("HealthNut Workspace", NavigationMode.Tabs) {
    init {

        Database.connect("jdbc:sqlite:./app.db", "org.sqlite.JDBC")
        createTables()

        // start controllers
        StatsController()
        DiaryEntryController()
        FoodController()

        // load initial data to display to the user
        fire(FoodEventModel.RefreshRequest)
        fire(DiaryEntryEventModel.RefreshRequest())
        fire(DiaryEntryEventModel.FilterByEntryDateRequest())

        dock<FoodListView>()
        dock<DiaryEntryMainView>()

        tabContainer.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        shortcut("Ctrl+R") { onRefresh() }
        shortcut("Ctrl+A") { onCreate() }
        shortcut("Ctrl+X") { onDelete() }
    }
}