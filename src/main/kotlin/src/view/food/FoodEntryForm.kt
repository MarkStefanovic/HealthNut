package src.view.food

import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import src.controller.FoodEventModel
import src.model.FoodModel
import src.utils.SafeIntegerConverter
import src.utils.tryInt
import tornadofx.*

class FoodEntryForm : Fragment("Add Food") {
    val model: FoodModel by inject()

    var nameField: TextField by singleAssign()
    var caloriesField: TextField by singleAssign()
    var saveButton: Button by singleAssign()
    var resetButton: Button by singleAssign()

    override val root = form {
        vgrow = Priority.NEVER
        fieldset {
            field("Name").apply {
                nameField = textfield(model.name) {
                    validator {
                        if (it.isNullOrBlank()) error("The name field is required") else null
                    }
                }
            }
            field("Calories") {
                maxWidth = 180.0
                caloriesField = textfield(model.calories, SafeIntegerConverter()) {
                    validator {
                        when {
                            it.isNullOrEmpty() -> error("The calories field is required.")
                            it?.tryInt() == 0 -> error("The calories field cannot be zero.")
                            else -> null
                        }
                    }
                }
            }
        }
        buttonbar {
            saveButton = button("Save") {

                enableWhen(model.valid)
                isDefaultButton = true
                action {
                    fire(FoodEventModel.AddRequest(model.name.value, model.calories.value.toInt()))
                    resetForm()
                }
            }
            resetButton = button("Reset") {
                action {
                    resetForm()
                }
            }
        }
    }

    fun resetForm() {
        nameField.text = ""
        caloriesField.text = "0"
        nameField.requestFocus()
    }
}
