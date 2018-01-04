package src.view.food

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import src.controller.FoodEventModel
import tornadofx.*

class FoodEditor : View("Foods") {
    val foodListView: FoodListView by inject()

    override val root = borderpane {
        padding = Insets(5.0)

        center {
            add(foodListView)
        }

        shortcut("Ctrl+R") { onRefresh() }
        shortcut("Ctrl+A") { onCreate() }
        shortcut("Ctrl+X") { onDelete() }
    }

    override fun onDock() {
//        find(FoodSearch::class).removeFromParent()
        workspace.add(FoodSearch::class)
        workspace.saveButton.removeFromParent()
    }

    override fun onCreate() {
        FoodEntryForm().openModal()
    }

    override fun onDelete() {
        foodListView.onDelete()
    }

    override fun onRefresh() {
        fire(FoodEventModel.RefreshRequest())
    }
}


class FoodSearch : View() {
    val nameFilterProperty = SimpleStringProperty().apply {
        onChange { fire(FoodEventModel.FilterByNameRequest(value ?: "")) }
    }

    override val root = hbox {
        textfield(nameFilterProperty) {
            promptText = "Food Name Contains"
        }
    }
}