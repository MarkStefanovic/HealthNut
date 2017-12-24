package src.controller

import org.jetbrains.exposed.sql.*
import src.app.execute
import src.model.DiaryEntries
import src.model.DiaryEntry
import src.model.toDiaryEntry
import src.utils.toDate
import tornadofx.*
import java.time.LocalDate


object DiaryEntryEventModel {
    class AddRequest(val newEntryDate: LocalDate, val newDescription: String = "", val newCalories: Int = 0) : FXEvent(EventBus.RunOn.BackgroundThread)
    class AddEvent(val item: DiaryEntry) : FXEvent()

    class FilterByEntryDateRequest(val entryDate: LocalDate = LocalDate.now()) : FXEvent(EventBus.RunOn.BackgroundThread)

    class UpdateCaloriesRequest(val id: Int, val updatedCalories: Int) : FXEvent(EventBus.RunOn.BackgroundThread)
    class UpdateDescriptionRequest(val id: Int, val updatedDescription: String) : FXEvent(EventBus.RunOn.BackgroundThread)
    class UpdateEntryDateRequest(val id: Int, val updatedEntryDate: LocalDate) : FXEvent(EventBus.RunOn.BackgroundThread)
    class UpdateEvent(val id: Int) : FXEvent()

    class DeleteRequest(val id: Int) : FXEvent(EventBus.RunOn.BackgroundThread)
    class DeleteEvent(val id: Int) : FXEvent()

    class RefreshRequest : FXEvent(EventBus.RunOn.BackgroundThread)
    class RefreshEvent(val items: List<DiaryEntry>) : FXEvent()
}


class DiaryEntryController : Controller() {

    init {
        subscribe<DiaryEntryEventModel.AddRequest> { fire(DiaryEntryEventModel.AddEvent(add(it.newEntryDate, it.newDescription, it.newCalories))) }
        subscribe<DiaryEntryEventModel.UpdateCaloriesRequest> { fire(DiaryEntryEventModel.UpdateEvent(updateCalories(it.id, it.updatedCalories))) }
        subscribe<DiaryEntryEventModel.UpdateDescriptionRequest> { fire(DiaryEntryEventModel.UpdateEvent(updateDescription(it.id, it.updatedDescription))) }
        subscribe<DiaryEntryEventModel.UpdateEntryDateRequest> { fire(DiaryEntryEventModel.UpdateEvent(updateEntryDate(it.id, it.updatedEntryDate))) }
        subscribe<DiaryEntryEventModel.DeleteRequest> { fire(DiaryEntryEventModel.DeleteEvent(delete(it.id))) }
        subscribe<DiaryEntryEventModel.RefreshRequest> { fire(DiaryEntryEventModel.RefreshEvent(refresh())) }
        subscribe<DiaryEntryEventModel.FilterByEntryDateRequest> { fire(DiaryEntryEventModel.RefreshEvent(filterByEntryDate(it.entryDate))) }

        fire(DiaryEntryEventModel.RefreshRequest())
        fire(DiaryEntryEventModel.FilterByEntryDateRequest())
    }

    private fun add(newEntryDate: LocalDate, newDescription: String, newCalories: Int): DiaryEntry {
        val newEntry = execute {
            DiaryEntries.insert {
                it[entryDate] = newEntryDate.toDate()
                it[description] = newDescription
                it[calories] = newCalories
            }
        }
        return DiaryEntry(newEntry[DiaryEntries.id], newEntryDate, newDescription, newCalories)
    }

    private fun updateCalories(id: Int, updatedCalories: Int) = execute {
        DiaryEntries.update({ DiaryEntries.id eq id }) {
            it[calories] = updatedCalories
        }
    }

    private fun updateEntryDate(id: Int, updatedEntryDate: LocalDate) = execute {
        DiaryEntries.update({ DiaryEntries.id eq id }) {
            it[entryDate] = updatedEntryDate.toDate()
        }
    }

    private fun updateDescription(id: Int, updatedDescription: String) = execute {
        DiaryEntries.update({ DiaryEntries.id eq id }) {
            it[description] = updatedDescription
        }
    }

    private fun delete(id: Int) = execute { DiaryEntries.deleteWhere { DiaryEntries.id eq id } }

    private fun refresh(): List<DiaryEntry> = execute {
        DiaryEntries.selectAll().map { it.toDiaryEntry() }
    }

    private fun filterByEntryDate(entryDate: LocalDate): List<DiaryEntry> = execute {
        DiaryEntries.select({ DiaryEntries.entryDate eq entryDate.toDate() }).map { it.toDiaryEntry() }
    }
}