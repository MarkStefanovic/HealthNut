package src.view.diary

import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.layout.Priority
import src.app.Styles
import src.controller.DiaryEntryEventModel
import src.model.DiaryEntry
import src.utils.IntegerConverter
import src.utils.SafeDateStringConverter
import tornadofx.*


class DiaryEntryListView : View() {
    val selectedId = SimpleIntegerProperty(-1)

    override val root = tableview<DiaryEntry> {
        isEditable = true
        title = "Entry Date"
        vgrow = Priority.ALWAYS
        val nameCol = column("Entry Date", DiaryEntry::entryDateProperty).apply {
            makeEditable(SafeDateStringConverter())
            fixedWidth(100.0)
            addClass(Styles.leftAlignedCell)
            setOnEditCommit {
                fire(DiaryEntryEventModel.UpdateEntryDateRequest(it.rowValue.id, it.newValue))
                selectionModel.selectNext()
            }
        }
        column("Description", DiaryEntry::description).apply {
            makeEditable()
            prefWidth = 200.0
            setOnEditCommit {
                fire(DiaryEntryEventModel.UpdateDescriptionRequest(it.rowValue.id, it.newValue))
                selectionModel.selectNext()
            }
        }
        column("Calories", DiaryEntry::calories).apply {
            makeEditable(IntegerConverter)
            addClass(Styles.rightAlignedCell)
            fixedWidth(80.0)
            setOnEditCommit {
                fire(DiaryEntryEventModel.UpdateCaloriesRequest(it.rowValue.id, it.newValue))
                selectionModel.selectNext()
            }
        }

        onSelectionChange {
            selectedId.set(it?.id ?: -1)
        }
        regainFocusAfterEdit()
        enableCellEditing()

        subscribe<DiaryEntryEventModel.AddEvent> { event ->
            if (event.item != null) {
                requestFocus()
                val nextRow = items.lastIndex + 1
                items.add(nextRow, event.item)
                scrollTo(nextRow)
                selectionModel.select(nextRow, nameCol)
                edit(nextRow, nameCol)
            }
        }
        subscribe<DiaryEntryEventModel.DeleteEvent> { event -> items.remove(selectedItem) }
        subscribe<DiaryEntryEventModel.RefreshEvent> { event -> items.setAll(event.items) }
    }

    override fun onRefresh() = fire(DiaryEntryEventModel.FilterByEntryDateRequest())
    override fun onDelete() {
        if (selectedId.value != -1) fire(DiaryEntryEventModel.DeleteRequest(selectedId.value))
    }
}

