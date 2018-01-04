package src.app

import javafx.scene.control.TabPane
import org.jetbrains.exposed.sql.Database
import src.controller.*
import src.utils.createTables
import src.utils.enableConoleLogger
import src.view.diary.DiaryEntryEditor
import src.view.food.FoodEditor
import tornadofx.*

class HealthNutWorkspace : Workspace("HealthNut Workspace", NavigationMode.Tabs) {
    init {
//        saveButton.isVisible = false

        enableConoleLogger()
        Database.connect("jdbc:sqlite:./app.db", "org.sqlite.JDBC")
        createTables()

        // start controllers
        StatsController()
        DiaryEntryController()
        FoodController()

        // load initial data to display to the user
        fire(FoodEventModel.RefreshRequest())
        fire(DiaryEntryEventModel.RefreshRequest())
        fire(DiaryEntryEventModel.FilterByEntryDateRequest())

        dock<FoodEditor>()
        dock<DiaryEntryEditor>()

        tabContainer.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
    }

    override fun onDock() {
        workspace.tabContainer.tabs[1].select()
    }
}