package src.controller

import org.jetbrains.exposed.sql.*
import src.utils.execute
import src.model.DiaryEntries
import src.model.DiaryEntry
import src.model.toDiaryEntry
import src.utils.toDate
import tornadofx.*
import java.math.BigDecimal
import java.time.LocalDate


object DiaryEntryEventModel {
    class AddRequest(val newEntryDate: LocalDate, val newDescription: String = "", val newQuantity: Double = 0.0, val newCaloriesPerUnit: Int = 0) : FXEvent(EventBus.RunOn.BackgroundThread)
    class AddEvent(val item: DiaryEntry) : FXEvent()

    class UpdateRequest(val updatedItem: DiaryEntry) : FXEvent(EventBus.RunOn.BackgroundThread)
    class UpdateEvent(val item: DiaryEntry) : FXEvent()

    class DeleteRequest(val id: Int) : FXEvent(EventBus.RunOn.BackgroundThread)
    class DeleteEvent(val id: Int) : FXEvent()

    class RefreshRequest : FXEvent(EventBus.RunOn.BackgroundThread)
    class RefreshEvent(val items: List<DiaryEntry>) : FXEvent()

    class FilterByEntryDateRequest(val entryDate: LocalDate = LocalDate.now()) : FXEvent(EventBus.RunOn.BackgroundThread)
}


class DiaryEntryController : Controller() {

    init {
        subscribe<DiaryEntryEventModel.AddRequest> {
            fire(DiaryEntryEventModel.AddEvent(add(it.newEntryDate, it.newDescription, it.newQuantity, it.newCaloriesPerUnit)))
        }
        subscribe<DiaryEntryEventModel.UpdateRequest> {
            fire(DiaryEntryEventModel.UpdateEvent(update(it.updatedItem)))
        }
        subscribe<DiaryEntryEventModel.DeleteRequest> {
            fire(DiaryEntryEventModel.DeleteEvent(delete(it.id)))
        }
        subscribe<DiaryEntryEventModel.RefreshRequest> {
            fire(DiaryEntryEventModel.RefreshEvent(refresh()))
        }
        subscribe<DiaryEntryEventModel.FilterByEntryDateRequest> {
            fire(DiaryEntryEventModel.RefreshEvent(filterByEntryDate(it.entryDate)))
        }
    }

    private fun add(newEntryDate: LocalDate, newDescription: String, newQuantity: Double, newCaloriesPerUnit: Int): DiaryEntry {
        val newEntry = execute {
            DiaryEntries.insert {
                it[entryDate] = newEntryDate.toDate()
                it[description] = newDescription
                it[quantity] = BigDecimal.valueOf(newQuantity)
                it[caloriesPerUnit] = newCaloriesPerUnit
            }
        }
        return DiaryEntry(newEntry[DiaryEntries.id], newEntryDate, newDescription, newQuantity, newCaloriesPerUnit)
    }

    private fun update(updatedItem: DiaryEntry): DiaryEntry {
        execute {
            DiaryEntries.update({ DiaryEntries.id eq updatedItem.id }) {
                it[entryDate] = updatedItem.entryDate.toDate()
                it[description] = updatedItem.description
                it[quantity] = BigDecimal.valueOf(updatedItem.quantity)
                it[caloriesPerUnit] = updatedItem.caloriesPerUnit
            }
        }
        return updatedItem
    }

    private fun delete(id: Int) = execute {
        DiaryEntries.deleteWhere { DiaryEntries.id eq id }
    }

    private fun refresh(): List<DiaryEntry> = execute {
        DiaryEntries
                .selectAll()
                .map { it.toDiaryEntry() }
    }

    private fun filterByEntryDate(entryDate: LocalDate): List<DiaryEntry> = execute {
        DiaryEntries
                .select({ DiaryEntries.entryDate eq entryDate.toDate() })
                .map { it.toDiaryEntry() }
    }
}