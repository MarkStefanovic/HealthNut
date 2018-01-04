package src.model

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import src.utils.toJavaLocalDate
import tornadofx.*
import java.time.LocalDate


fun ResultRow.toDiaryEntry() = DiaryEntry(
        this[DiaryEntries.id],
        this[DiaryEntries.entryDate].toJavaLocalDate(),
        this[DiaryEntries.description],
        this[DiaryEntries.quantity].toDouble(),
        this[DiaryEntries.caloriesPerUnit]
)

object DiaryEntries : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val entryDate = date("entry_date")
    val description = varchar("description", 120)
    val quantity = decimal("quantity", 9, 2)
    val caloriesPerUnit = integer("calories")
}

class DiaryEntry(id: Int, entryDate: LocalDate, description: String, quantity: Double, caloriesPerUnit: Int) {
    val idProperty = SimpleIntegerProperty(id)
    var id by idProperty

    val entryDateProperty = SimpleObjectProperty<LocalDate>(entryDate)
    var entryDate: LocalDate by entryDateProperty

    val descriptionProperty = SimpleStringProperty(description)
    var description: String by descriptionProperty

    val quantityProperty = SimpleDoubleProperty(quantity)
    var quantity: Double by quantityProperty

    val caloriesPerUnitProperty = SimpleIntegerProperty(caloriesPerUnit)
    var caloriesPerUnit: Int by caloriesPerUnitProperty

    val totalCalories = Bindings.multiply(caloriesPerUnitProperty, quantityProperty)

    override fun toString() = "DiaryEntry(id=$id, entryDate=$entryDate, description=$description, quantity=$quantity, caloriesPerUnit=$caloriesPerUnit)"
}

class DiaryEntryModel : ItemViewModel<DiaryEntry>() {
    val id = bind(DiaryEntry::idProperty)
    val entryDate = bind(DiaryEntry::entryDateProperty)
    val description = bind(DiaryEntry::descriptionProperty)
    val caloriesPerUnit = bind(DiaryEntry::caloriesPerUnitProperty)
    val quantity = bind(DiaryEntry::quantityProperty)
    val totalCalories = itemProperty.select(DiaryEntry::totalCalories)
}
