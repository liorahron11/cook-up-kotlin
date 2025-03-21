package com.example.cookup.room.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookup.models.Recipe
import com.example.cookup.models.RecipeWithUser
import com.example.cookup.models.SpoonacularClient
import com.example.cookup.models.User
import com.example.cookup.models.toAppRecipe
import com.example.cookup.room.RecipeRemoteDataSource
import com.example.cookup.room.repositories.RecipeRepository
import com.example.cookup.utils.toEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {
    private val remoteDataSource = RecipeRemoteDataSource()
    val spoonacularApi = SpoonacularClient.create()
    private val repository = RecipeRepository(application, RecipeRemoteDataSource(), spoonacularApi)
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    private val _spoonacularFlow = MutableStateFlow<List<Recipe>>(emptyList())
    val spoonacularFlow: StateFlow<List<Recipe>> = _spoonacularFlow
    val recipeFeed: StateFlow<List<RecipeWithUser>> = combine(
        repository.localRecipesWithUserFlow,
        spoonacularFlow
    ) { local, remote ->
        val fakeUser: User = User("Spoonacular", "Spoonacular", "Spoonacular", "https://play-lh.googleusercontent.com/uOZlIZUJ7R79qs_J_a9cdxrJaGhHwqKTmika25Lp1vTeC1qe9lPQF5jalEFc8Htk7nQ")
        val remoteWrapped = remote.map { recipe ->
            RecipeWithUser(recipe = recipe, user = fakeUser)
        }
        local + remoteWrapped
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun fetchSpoonacularRecipes(count: Int = 10) {
        viewModelScope.launch {
            try {
                val apiKey: String = "165c6099f12842b1b3996b779d6fd6ef"
                _isRefreshing.value = true
                val response = spoonacularApi.getRandomRecipes(count, apiKey)
                val fetched = response.recipes.map { it.toAppRecipe() }
                _spoonacularFlow.value = fetched
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    init {
        viewModelScope.launch {
            fetchAllData()
        }
    }

    fun fetchAllData() {
        viewModelScope.launch {
            repository.refreshRecipesFromRemote()
            fetchSpoonacularRecipes()
        }
    }

    fun toggleLike(recipe: Recipe) {
        viewModelScope.launch {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val updatedLikes = if (recipe.likes.contains(currentUserId)) {
                recipe.likes - currentUserId
            } else {
                recipe.likes + currentUserId
            }
            val updatedRecipe = recipe.copy(likes = updatedLikes)
            repository.insertRecipe(updatedRecipe.toEntity())
            remoteDataSource.updateRecipeLikes(recipe.id, updatedLikes)
        }

    }
}
