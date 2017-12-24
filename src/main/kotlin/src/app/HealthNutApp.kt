package src.app

import javafx.scene.Scene
import javafx.scene.control.TabPane
import javafx.scene.image.Image
import org.jetbrains.exposed.sql.Database
import src.controller.DiaryEntryController
import src.controller.FoodController
import src.controller.StatsController
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

        StatsController()
        DiaryEntryController()
        FoodController()

        dock<FoodListView>()
        dock<DiaryEntryMainView>()

        tabContainer.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        shortcut("Ctrl+R") { onRefresh() }
        shortcut("Ctrl+A") { onCreate() }
        shortcut("Ctrl+X") { onDelete() }

//        disableSave()
    }

//    override val savable = SimpleBooleanProperty(false)
}

//
//class HealthNutWorkspace : Workspace("HealthNut Workspace", NavigationMode.Stack) {
//    val foodListView: FoodListView by inject()
//    val diaryEditor: DiaryEntryListView by inject()
//
//    init {
//        // initialize globals and controllers
//        db.connect()
//        FoodController()
//
//        // set up views
//        with (leftDrawer) {
//            multiselect = true
////            floatingDrawers = true
////            hgrow = Priority.ALWAYS
//
//                item(foodListView, true)
//                item(diaryEditor, true)
//
//        }
//    }
//}