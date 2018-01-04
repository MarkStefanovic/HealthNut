package src.view.diary

import javafx.geometry.Insets
import javafx.scene.layout.Priority
import src.view.food.FoodSearch
import tornadofx.*

class DiaryEntryEditor : View("Diary") {
    val diaryEntryForm: DiaryEntryForm by inject()
    val diaryEntryListView: DiaryEntryListView by inject()
    val statsView: StatsView by inject()

    override val root = borderpane {
        padding = Insets(5.0)
//        hgrow = Priority.ALWAYS

        left { add(diaryEntryForm) }
        center { add(diaryEntryListView) }
        right { add(statsView) }

        disableCreate()
        forwardWorkspaceActions(diaryEntryListView)

        shortcut("Ctrl+R") { onRefresh() }
        shortcut("Ctrl+X") { onDelete() }
    }

    override fun onDock() {
        workspace.saveButton.removeFromParent()
        find(FoodSearch::class).removeFromParent()
    }
}
