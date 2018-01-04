package src.view.food

import javafx.scene.layout.Priority
import src.app.Styles
import src.controller.FoodEventModel
import src.model.Food
import src.model.FoodModel
import src.utils.IntegerConverter
import tornadofx.*


class FoodListView : View() {
    val model: FoodModel by inject()

    override val root = vbox {
        tableview<Food> {
            title = "Foods"
            vgrow = Priority.ALWAYS
            val nameCol = column("Name", Food::name).apply {
                makeEditable()
                prefWidth = 200.0
            }
            column("Calories", Food::calories).apply {
                makeEditable(IntegerConverter)
                addClass(Styles.rightAlignedCell)
                fixedWidth(80.0)
            }
            column("Favorite", Food::favoriteProperty).useCheckbox(editable = true)
            regainFocusAfterEdit()
            enableCellEditing()

            onEditCommit {
                fire(FoodEventModel.UpdateRequest(it))
                selectionModel.selectNext()
            }

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
                items.setAll(event.items)
            }
            subscribe<FoodEventModel.FilterByNameEvent> {
                event -> items.setAll(event.items)
            }

            bindSelected(model)

            contextmenu {
                item("Send Email").action {
                    selectedItem?.apply { println("Sending Email to $name") }
                }
                item("Change Status").action {
                    selectedItem?.apply { println("Changing Status for $name") }
                }
            }
        }
    }

    override fun onDelete() {
        model.item?.let {
            fire(FoodEventModel.DeleteRequest(model.item.id))
        }
    }
}