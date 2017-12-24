package src.model

import javafx.beans.property.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import src.utils.toJavaLocalDate
import tornadofx.*
import java.time.LocalDate


fun ResultRow.toDiaryEntry() = DiaryEntry(
        this[DiaryEntries.id],
        this[DiaryEntries.entryDate].toJavaLocalDate(),
        this[DiaryEntries.description],
        this[DiaryEntries.calories]
)

object DiaryEntries : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val entryDate = date("entry_date")
    val description = varchar("description", 120)
    val calories = integer("calories")
}

class DiaryEntry(id: Int = -1, entryDate: LocalDate = LocalDate.now(), description: String = "", calories: Int = 0) {
    val idProperty = SimpleIntegerProperty(id)
    var id by idProperty

    val entryDateProperty = SimpleObjectProperty<LocalDate>(entryDate)
    var entryDate: LocalDate by entryDateProperty

    val descriptionProperty = SimpleStringProperty(description)
    var description: String by descriptionProperty

    val caloriesProperty = SimpleIntegerProperty(calories)
    var calories: Int by caloriesProperty

    override fun toString() = "DiaryEntry(id=$id, entryDate=$entryDate, description=$description, calories=$calories)"
}

class DiaryEntryModel(entry: DiaryEntry = DiaryEntry()) : ItemViewModel<DiaryEntry>(entry) {
    val id = bind(DiaryEntry::idProperty)
    val entryDate = bind(DiaryEntry::entryDateProperty)
    val description = bind(DiaryEntry::descriptionProperty)
    val calories = bind(DiaryEntry::caloriesProperty)
}