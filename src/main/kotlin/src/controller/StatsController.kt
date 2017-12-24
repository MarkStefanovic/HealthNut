package src.controller

import org.jetbrains.exposed.sql.selectAll
import src.app.execute
import src.model.DiaryEntries
import src.model.toDiaryEntry
import tornadofx.*
import java.time.LocalDate

object StatsEventModel {
    object RefreshRequest : FXEvent(EventBus.RunOn.BackgroundThread)
    class RefreshEvent(val stats: Map<String, Int>) : FXEvent()
}

data class DailyTotal(val entryDate: LocalDate, val calories: Int)

class StatsController : Controller() {
    val today = LocalDate.of(LocalDate.now().year, LocalDate.now().month, LocalDate.now().dayOfMonth)

    init {
        subscribe<DiaryEntryEventModel.AddEvent> { fire(StatsEventModel.RefreshEvent(refresh())) }
        subscribe<DiaryEntryEventModel.DeleteEvent> { fire(StatsEventModel.RefreshEvent(refresh())) }
        subscribe<DiaryEntryEventModel.RefreshEvent> { fire(StatsEventModel.RefreshEvent(refresh())) }
        subscribe<DiaryEntryEventModel.UpdateEvent> { fire(StatsEventModel.RefreshEvent(refresh())) }

        subscribe<StatsEventModel.RefreshRequest> { fire(StatsEventModel.RefreshEvent(refresh())) }
    }

    private fun averageByDays(caloriesByDay: List<DailyTotal>, days: Long): Int = caloriesByDay
            .filter { it.entryDate.plusDays(days) >= today && it.entryDate < today }
            .map { it.calories }
            .average()
            .toInt()

    private fun refresh(): Map<String, Int> {
        val entries = execute { DiaryEntries.selectAll().map { it.toDiaryEntry() } }

        val dailyTotals: List<DailyTotal> = entries
                .groupBy { it.entryDate }
                .mapValues { it.value.sumBy { it.calories } }
                .map { DailyTotal(it.key, it.value) }
                .sortedByDescending { it.entryDate }
                .filter { it.entryDate <= LocalDate.now() }

        val todaysTotal = dailyTotals.find { it.entryDate == today }?.calories ?: 0

        return mapOf(
                "todaysTotal" to todaysTotal,
                "threeDayAverage" to averageByDays(dailyTotals, 3),
                "tenDayAverage" to averageByDays(dailyTotals, 10)
        )
    }
}
