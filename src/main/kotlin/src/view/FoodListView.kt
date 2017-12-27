package src.view

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.layout.Priority
import src.app.Styles
import src.controller.FoodEventModel
import src.model.Food
import src.utils.IntegerConverter
import tornadofx.*


class FoodListView : View() {
    val selectedId = SimpleIntegerProperty(-1)

    val nameFilterProperty = SimpleStringProperty().apply {
        onChange { fire(FoodEventModel.FilterByNameRequest(value ?: "")) }
    }

    override val root = vbox {
        padding = Insets(5.0)

        hbox {
            paddingBottom = 5.0

            label("Name like:")
            textfield(nameFilterProperty)
        }

        tableview<Food> {
            isEditable = true
            title = "Foods"
            vgrow = Priority.ALWAYS
            val nameCol = column("Name", Food::name).apply {
                makeEditable()
                prefWidth = 200.0
                setOnEditCommit {
                    fire(FoodEventModel.UpdateNameRequest(it.rowValue.id, it.newValue))
                    selectionModel.selectNext()
                }
            }
            column("Calories", Food::calories).apply {
                makeEditable(IntegerConverter)
                addClass(Styles.rightAlignedCell)
                fixedWidth(80.0)
                setOnEditCommit {
                    fire(FoodEventModel.UpdateCaloriesRequest(it.rowValue.id, it.newValue))
                    selectionModel.selectNext()
                }
            }
            column("Favorite", Food::favoriteProperty).apply {
                useCheckbox(editable = true)
                setOnEditStart {
                    fire(FoodEventModel.UpdateFavoriteRequest(it.rowValue.id, !it.oldValue))
                }
            }
            regainFocusAfterEdit()
            enableCellEditing()

            subscribe<FoodEventModel.AddEvent> { event ->
                requestFocus()
                val nextRow = items.lastIndex + 1
                items.add(nextRow, event.item)
                scrollTo(nextRow)
                selectionModel.select(nextRow, nameCol)
                edit(nextRow, nameCol)
            }
            subscribe<FoodEventModel.DeleteEvent> {
                items.remove(selectedItem)
            }
            subscribe<FoodEventModel.RefreshEvent> { event ->
                nameFilterProperty.set(null)
                items.setAll(event.items)
            }
            subscribe<FoodEventModel.FilterByNameEvent> {
                event -> items.setAll(event.items)
            }

            disableSave()
            onSelectionChange {
                selectedId.set(it?.id ?: -1)
            }
        }
    }

    override fun onCreate() = fire(FoodEventModel.AddRequest())
    override fun onSave() = fire(FoodEventModel.SaveRequest)
    override fun onRefresh() = fire(FoodEventModel.RefreshRequest)
    override fun onDelete() {
        if (selectedId.value != -1) fire(FoodEventModel.DeleteRequest(selectedId.value))
    }
}