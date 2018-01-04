package src.view.diary

import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import src.app.Styles.Companion.rightAlignedCell
import src.controller.DiaryEntryEventModel
import src.controller.FoodEventModel
import src.model.DiaryEntryModel
import src.model.Food
import src.utils.SafeDoubleConverter
import src.utils.SafeIntegerConverter
import src.utils.tryInt
import tornadofx.*
import java.time.LocalDate

class DiaryEntryForm : View() {
    val model: DiaryEntryModel by inject()

    var descriptionField: TextField by singleAssign()
    var caloriesPerUnitField: TextField by singleAssign()
    var entryDatePicker: DatePicker by singleAssign()
    var quantityField: TextField by singleAssign()
    var totalCaloriesLabel: Label by singleAssign()

    val totalCaloriesProperty = SimpleIntegerProperty(0)

    private fun updateTotalCalories() {
        try {
            val cal = (model.caloriesPerUnit.value.toDouble() * model.quantity.value.toDouble()).toInt()
            totalCaloriesProperty.set(cal)
        } catch (e: Exception) {
            totalCaloriesProperty.set(0)
        }
    }

    init {
        model.entryDate.value = LocalDate.now()
        model.entryDate.onChange {
            fire(DiaryEntryEventModel.FilterByEntryDateRequest(it ?: LocalDate.now()))
        }
        model.caloriesPerUnit.onChange {
            updateTotalCalories()
        }
        model.quantity.onChange {
            updateTotalCalories()
        }
    }

    override val root = vbox {
        form {
            vgrow = Priority.NEVER
            fieldset {
                field("Date") {
                    maxWidth = 220.0
                    entryDatePicker = datepicker(model.entryDate)
                }
                field("Description") {
                    descriptionField = textfield(model.description) {
                        validator {
                            when {
                                it.isNullOrEmpty() -> error("The description cannot be blank")
                                it!!.length < 3 -> error("Too short")
                                else -> null
                            }
                        }
                    }
                }
                field("Calories per") {
                    maxWidth = 180.0
                    caloriesPerUnitField = textfield(model.caloriesPerUnit, SafeIntegerConverter()) {
                        validator {
                            when {
                                it == null -> error("The calories per unit field cannot be blank")
                                it.tryInt() == 0 -> error("The description field cannot be zero")
                                else -> null
                            }
                        }
                    }
                }
                field("Units") {
                    maxWidth = 180.0
                    quantityField = textfield(model.quantity, SafeDoubleConverter()) {
                        validator {
                            when {
                                it == null -> error("The quantity field cannot be blank")
                                it.tryInt() == 0 -> error("The quantity field cannot be zero")
                                else -> null
                            }
                        }
                    }
                }
                field("Total Calories") {
                    totalCaloriesLabel = label(totalCaloriesProperty)
                }
            }

            buttonbar {
                button("Save") {
                    enableWhen(model.valid)
                    isDefaultButton = true
                    action {
                        fire(DiaryEntryEventModel.AddRequest(
                                model.entryDate.value,
                                model.description.value,
                                model.quantity.value.toDouble(),
                                totalCaloriesProperty.value)
                        )
                    }
                }
                button("Reset").action {
                    resetForm()
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
                model.caloriesPerUnit.value = food.calories
                model.description.value = food.name
                model.quantity.value = 1.0

            }

            subscribe<FoodEventModel.RefreshEvent> { event ->
                items.setAll(event.items.filter { it.favorite })
            }

            subscribe<FoodEventModel.FavoritesChangedEvent> { event ->
                items.setAll(event.items)
            }
        }
    }

    fun resetForm() {
        model.entryDate.value = LocalDate.now()
        model.description.value = ""
        model.quantity.value = 0.0
        model.caloriesPerUnit.value = 0

        descriptionField.requestFocus()
    }
}

