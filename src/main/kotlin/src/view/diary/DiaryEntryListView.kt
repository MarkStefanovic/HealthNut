package src.view.diary

import javafx.scene.layout.Priority
import src.app.Styles
import src.controller.DiaryEntryEventModel
import src.model.DiaryEntry
import src.model.DiaryEntryModel
import src.utils.IntegerConverter
import src.utils.SafeDateStringConverter
import tornadofx.*


class DiaryEntryListView : View() {
    val model: DiaryEntryModel by inject()

    override val root = tableview<DiaryEntry> {
        isEditable = true
        title = "Entry Date"
        vgrow = Priority.ALWAYS
        val nameCol = column("Entry Date", DiaryEntry::entryDateProperty).apply {
            makeEditable(SafeDateStringConverter())
            fixedWidth(100.0)
            addClass(Styles.leftAlignedCell)
        }
        column("Description", DiaryEntry::description).apply {
            makeEditable()
            prefWidth = 200.0
        }
        column("Quantity", DiaryEntry::quantity).apply {
            makeEditable()
            addClass(Styles.rightAlignedCell)
            prefWidth = 80.0
        }
        column("Cal/u", DiaryEntry::caloriesPerUnit).apply {
            makeEditable(IntegerConverter)
            addClass(Styles.rightAlignedCell)
            fixedWidth(80.0)
        }
        column("Total", DiaryEntry::totalCalories).apply {
            prefWidth = 80.0
            addClass(Styles.rightAlignedCell)
        }
        onEditCommit {
            fire(DiaryEntryEventModel.UpdateRequest(it))
            selectionModel.selectNext()
        }

        bindSelected(model)
        regainFocusAfterEdit()
        enableCellEditing()

        subscribe<DiaryEntryEventModel.AddEvent> { event ->
            requestFocus()
            val nextRow = items.lastIndex + 1
            items.add(nextRow, event.item)
            scrollTo(nextRow)
            selectionModel.select(nextRow, nameCol)
            edit(nextRow, nameCol)
        }
        subscribe<DiaryEntryEventModel.DeleteEvent> { event -> items.remove(selectedItem) }
        subscribe<DiaryEntryEventModel.RefreshEvent> { event -> items.setAll(event.items) }
    }

    override fun onRefresh() = fire(DiaryEntryEventModel.FilterByEntryDateRequest())
    override fun onDelete() {
        model.item?.let {
            fire(DiaryEntryEventModel.DeleteRequest(model.item.id))
        }
    }
}

