package src.model

import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import tornadofx.*


fun ResultRow.toFood() = Food(
        this[Foods.id],
        this[Foods.name],
        this[Foods.calories],
        this[Foods.favorite]
)

object Foods : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 50)
    val calories = integer("calories")
    val favorite = bool("favorite").default(false)
}

class Food(id: Int = -1, name: String = "", calories: Int = 0, favorite: Boolean = false) {
    val idProperty = SimpleIntegerProperty(id)
    var id: Int by idProperty

    val nameProperty = SimpleStringProperty(name)
    var name: String by nameProperty

    val caloriesProperty = SimpleIntegerProperty(calories)
    var calories: Int by caloriesProperty

    val favoriteProperty = SimpleBooleanProperty(favorite)
    var favorite: Boolean by favoriteProperty

    override fun toString() = "Food(id=$id, name=$name, calories=$calories, favorite=$favorite)"
}

class FoodModel(food: Food = Food()) : ItemViewModel<Food>(food) {
    val id = bind(Food::id)
    val name = bind(Food::name)
    val calories = bind(Food::calories)
    val favorite = bind(Food::favorite)

    override fun onCommit(commits: List<Commit>) {
        commits.findChanged(name)?.let { println("Name changed from ${it.first} to ${it.second}") }
        commits.findChanged(calories)?.let { println("Calories changed from ${it.first} to ${it.second}") }
    }

    private fun <T> List<Commit>.findChanged(ref: Property<T>): Pair<T, T>? {
        val commit = find { it.property == ref && it.changed }
        return commit?.let { (it.newValue as T) to (it.oldValue as T) }
    }
}



