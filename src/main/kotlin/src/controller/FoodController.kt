package src.controller

import org.jetbrains.exposed.sql.*
import src.utils.execute
import src.model.Food
import src.model.Foods
import src.model.toFood
import tornadofx.*


object FoodEventModel {
    class AddRequest(val newName: String = "", val newCalories: Int = 0) : FXEvent(EventBus.RunOn.BackgroundThread)
    class AddEvent(val item: Food) : FXEvent()

    class FilterByNameRequest(val nameLike: String = "") : FXEvent(EventBus.RunOn.BackgroundThread)
    class FilterByNameEvent(val items: List<Food>?) : FXEvent()

    class UpdateRequest(val item: Food) : FXEvent(EventBus.RunOn.BackgroundThread)
    class UpdateEvent(val id: Int) : FXEvent()

    class FavoritesChangedEvent(val items: List<Food>) : FXEvent()

    class DeleteRequest(val id: Int) : FXEvent(EventBus.RunOn.BackgroundThread)
    class DeleteEvent(val id: Int) : FXEvent()

    class RefreshRequest : FXEvent(EventBus.RunOn.BackgroundThread)
    class RefreshEvent(val items: List<Food>) : FXEvent()
}

class FoodController : Controller() {

    init {
        subscribe<FoodEventModel.AddRequest> {
            fire(FoodEventModel.AddEvent(add(it.newName, it.newCalories)))
        }
        subscribe<FoodEventModel.UpdateRequest> {
            fire(FoodEventModel.UpdateEvent(update(it.item)))
        }
        subscribe<FoodEventModel.DeleteRequest> {
            fire(FoodEventModel.DeleteEvent(delete(it.id)))
        }
        subscribe<FoodEventModel.RefreshRequest> {
            fire(FoodEventModel.RefreshEvent(getAll()))
        }
        subscribe<FoodEventModel.FilterByNameRequest> {
            fire(FoodEventModel.FilterByNameEvent(filterByName(it.nameLike)))
        }
    }

    private fun add(newName: String, newCalories: Int): Food {
        val newFood = execute {
            Foods.insert {
                it[name] = newName
                it[calories] = newCalories
            }
        }
        return Food(newFood[Foods.id], newName, newCalories)
    }

    private fun update(item: Food): Int {
        val id = execute {
            Foods.update({ Foods.id eq item.id }) {
                it[name] = item.name
                it[calories] = item.calories
                it[favorite] = item.favorite
            }
        }
        fire(FoodEventModel.FavoritesChangedEvent(getAll().filter { it.favorite }))
        return id
    }

    private fun delete(id: Int) = execute {
        Foods.deleteWhere { Foods.id eq id }
    }

    private fun getAll(): List<Food> = execute {
        Foods.selectAll().map { it.toFood() }
    }

    private fun filterByName(name: String): List<Food> = execute {
        Foods.select { Foods.name like "%$name%" }.map { it.toFood() }
    }
}
