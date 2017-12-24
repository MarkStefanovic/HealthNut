package src.view

import javafx.beans.property.SimpleIntegerProperty
import src.controller.StatsEventModel
import tornadofx.*

class StatsView : View("My View") {
    val todaysTotalProperty = SimpleIntegerProperty()
    val threeDayAverageProperty = SimpleIntegerProperty()
    val tenDayAverageProperty = SimpleIntegerProperty()

    override val root = form {

        fieldset {
            field("Net Calories") {
                label(todaysTotalProperty)
            }
            field("3 day average") {
                label(threeDayAverageProperty)
            }
            field("10 day average") {
                label(tenDayAverageProperty)
            }
        }

        subscribe<StatsEventModel.RefreshEvent> { event ->
            todaysTotalProperty.set(event.stats["todaysTotal"] ?: 0)
            threeDayAverageProperty.set(event.stats["threeDayAverage"] ?: 0)
            tenDayAverageProperty.set(event.stats["tenDayAverage"] ?: 0)
        }
    }
}