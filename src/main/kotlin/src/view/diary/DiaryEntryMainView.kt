package src.view.diary

import javafx.geometry.Insets
import javafx.scene.layout.Priority
import src.view.StatsView
import tornadofx.*

class DiaryEntryMainView : View("Diary") {
    val diaryEntryForm: DiaryEntryForm by inject()
    val diaryEntryListView: DiaryEntryListView by inject()
    val statsView: StatsView by inject()

    override val root = borderpane {
        padding = Insets(5.0)
        hgrow = Priority.ALWAYS

        left { add(diaryEntryForm) }
        center { add(diaryEntryListView) }
        right { add(statsView) }

        disableSave()
    }

    override fun onCreate() {
        diaryEntryForm.onCreate()
    }

    override fun onDelete() {
        diaryEntryListView.onDelete()
    }

    override fun onRefresh() {
        diaryEntryListView.onRefresh()
    }

    override fun onSave() {
        diaryEntryListView.onSave()
    }
}
