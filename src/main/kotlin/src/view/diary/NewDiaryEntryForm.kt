package src.view.diary

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import src.app.Styles.Companion.rightAlignedCell
import src.controller.DiaryEntryEventModel
import src.controller.FoodEventModel
import src.model.Food
import src.utils.SafeIntegerConverter
import tornadofx.*
import java.time.LocalDate

class DiaryEntryForm : View() {
    val caloriesPerUnitProperty = SimpleIntegerProperty().apply {
        onChange { updateTotalCalories() }
    }
    val descriptionProperty = SimpleStringProperty().apply {
        onChange { updateIsValidProperty() }
    }
    val entryDateProperty = SimpleObjectProperty<LocalDate>(LocalDate.now()).apply {
        onChange { fire(DiaryEntryEventModel.FilterByEntryDateRequest(value ?: LocalDate.now())) }
    }
    val quantityProperty = SimpleIntegerProperty().apply {
        onChange { updateTotalCalories() }
    }
    val totalCaloriesProperty = SimpleIntegerProperty().apply {
        onChange { updateIsValidProperty() }
    }
    val isValidProperty = SimpleBooleanProperty()

    private fun updateIsValidProperty() {
        if (descriptionProperty.value == null || totalCaloriesProperty.value == null) {
            return isValidProperty.set(false)
        } else {
            if (descriptionProperty.value.isNotBlank() && totalCaloriesProperty.value != 0) {
                return isValidProperty.set(true)
            }
        }
        isValidProperty.set(false)
    }

    private fun updateTotalCalories() {
        val cals = caloriesPerUnitProperty.value.toInt() * quantityProperty.value.toInt()
        totalCaloriesProperty.set(cals)
    }

    override val root = vbox {
        form {
            vgrow = Priority.NEVER
            fieldset {
                field("Date") {
                    maxWidth = 220.0
                    datepicker(entryDateProperty)
                }
                field("Description") {
                    textfield(descriptionProperty)
                }
                field("Calories per") {
                    maxWidth = 180.0
                    textfield(caloriesPerUnitProperty, SafeIntegerConverter())
                }
                field("Units") {
                    maxWidth = 180.0
                    textfield(quantityProperty, SafeIntegerConverter())
                }
                field("Total Calories") {
                    label(totalCaloriesProperty)
                }
            }
            buttonbar {
                button("Save") {
                    enableWhen(isValidProperty)
                    action {
                        fire(DiaryEntryEventModel.AddRequest(entryDateProperty.value, descriptionProperty.value, totalCaloriesProperty.value))
                    }
                }
                button("Reset").action {
                    entryDateProperty.set(LocalDate.now())
                    caloriesPerUnitProperty.set(0)
                    descriptionProperty.set(null)
                    quantityProperty.set(0)
                }
            }
        }

        tableview<Food> {
            vgrow = Priority.ALWAYS
            column("Name", Food::name) {
                remainingWidth()
                prefWidth = 300.0
            }
            column("Calories", Food::calories) {
                addClass(rightAlignedCell)
                maxWidth = 80.0
            }

            smartResize()
            onSelectionChange {
                val food = selectedItem ?: Food()
                caloriesPerUnitProperty.set(food.calories)
                descriptionProperty.set(food.name)
                quantityProperty.set(1)
            }

            subscribe<FoodEventModel.RefreshEvent> { event ->
                items.setAll(event.items.filter { it.favorite })
            }

            subscribe<FoodEventModel.FavoritesChangedEvent> { event ->
                items.setAll(event.items)
            }
        }
    }
}

