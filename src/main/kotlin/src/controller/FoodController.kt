package src.controller

import javafx.scene.control.Alert
import org.jetbrains.exposed.sql.*
import src.app.execute
import src.model.Food
import src.model.Foods
import src.model.toFood
import tornadofx.*


object FoodEventModel {
    class AddRequest(val newName: String = "", val newCalories: Int = 0) : FXEvent(EventBus.RunOn.BackgroundThread)
    class AddEvent(val item: Food?) : FXEvent()

    class FilterByNameRequest(val nameLike: String = "") : FXEvent(EventBus.RunOn.BackgroundThread)
    class FilterByNameEvent(val items: List<Food>?) : FXEvent()

    class UpdateCaloriesRequest(val id: Int, val updatedCalories: Int) : FXEvent(EventBus.RunOn.BackgroundThread)
    class UpdateNameRequest(val id: Int, val updatedName: String) : FXEvent(EventBus.RunOn.BackgroundThread)
    class UpdateFavoriteRequest(val id: Int, val updatedFavorite: Boolean) : FXEvent(EventBus.RunOn.BackgroundThread)
    class FavoritesChangedEvent(val items: List<Food>) : FXEvent()

    class DeleteRequest(val id: Int) : FXEvent(EventBus.RunOn.BackgroundThread)
    class DeleteEvent(val id: Int) : FXEvent()

    object RefreshRequest : FXEvent(EventBus.RunOn.BackgroundThread)
    class RefreshEvent(val items: List<Food>) : FXEvent()

    object SaveRequest : FXEvent(EventBus.RunOn.BackgroundThread)
}

class FoodController : Controller() {

    init {
        subscribe<FoodEventModel.AddRequest> { fire(FoodEventModel.AddEvent(add(it.newName, it.newCalories))) }
        subscribe<FoodEventModel.UpdateCaloriesRequest> { updateCalories(it.id, it.updatedCalories) }
        subscribe<FoodEventModel.UpdateNameRequest> { updateName(it.id, it.updatedName) }
        subscribe<FoodEventModel.UpdateFavoriteRequest> { updateFavorite(it.id, it.updatedFavorite) }
        subscribe<FoodEventModel.DeleteRequest> { fire(FoodEventModel.DeleteEvent(delete(it.id))) }
        subscribe<FoodEventModel.RefreshRequest> { fire(FoodEventModel.RefreshEvent(refresh())) }
        subscribe<FoodEventModel.FilterByNameRequest> { fire(FoodEventModel.FilterByNameEvent(filterByName(it.nameLike))) }

        fire(FoodEventModel.RefreshRequest)
    }

    private fun add(newName: String, newCalories: Int): Food? {
        return try {
            val newFood = execute {
                Foods.insert {
                    it[name] = newName
                    it[calories] = newCalories
                }
            }
            Food(newFood[Foods.id], newName, newCalories)
        } catch (e: Exception) {
            runLater {
                alert(Alert.AlertType.ERROR, "Error", "Unable to add a row new row to the db.\nError: $e")
            }
            null
        }
    }

    private fun updateCalories(id: Int, updatedCalories: Int) = execute {
        Foods.update({ Foods.id eq id }) {
            it[calories] = updatedCalories
        }
    }

    private fun updateFavorite(id: Int, updatedFavorite: Boolean) {
        execute {
            Foods.update({ Foods.id eq id }) {
                it[favorite] = updatedFavorite
            }
        }
        fire(FoodEventModel.FavoritesChangedEvent(refresh().filter { it.favorite }))
    }

    private fun updateName(id: Int, updatedName: String) = execute {
        Foods.update({ Foods.id eq id }) {
            it[name] = updatedName
        }
    }

    private fun delete(id: Int) = execute { Foods.deleteWhere { Foods.id eq id } }

    private fun refresh(): List<Food> = execute { Foods.selectAll().map { it.toFood() } }

    private fun filterByName(name: String): List<Food> = execute {
        Foods.select { Foods.name like "%$name%" }.map { it.toFood() }
    }
}
