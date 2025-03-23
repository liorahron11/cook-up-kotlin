package com.example.cookup.room.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookup.models.Recipe
import com.example.cookup.models.RecipeWithUser
import com.example.cookup.room.repositories.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipeRepository(application)

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _spoonacularRecipes = MutableStateFlow<List<Recipe>>(emptyList())

    val recipeFeed: StateFlow<List<RecipeWithUser>> = combine(
        repository.localRecipesWithUserFlow,
        _spoonacularRecipes
    ) { local, remote ->
        val remoteWrapped = repository.wrapSpoonacularRecipes(remote)
        local + remoteWrapped
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        viewModelScope.launch {
            fetchAllData()
        }

        viewModelScope.launch {
            recipeFeed.collect { recipes ->
                if (recipes.isEmpty() && _isRefreshing.value) {
                    _uiState.value = FeedUiState.Loading
                } else {
                    _uiState.value = FeedUiState.Success(recipes)
                }
            }
        }
    }

    fun fetchAllData() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                repository.refreshRecipesFromRemote()
                fetchSpoonacularRecipes()
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error("Failed to refresh data: ${e.localizedMessage}")
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun fetchSpoonacularRecipes(count: Int = 10) {
        viewModelScope.launch {
            try {
                val recipes = repository.getSpoonacularRecipes(count)
                _spoonacularRecipes.value = recipes
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error("Failed to fetch spoonacular recipes: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    fun toggleLike(recipe: Recipe) {
        viewModelScope.launch {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                repository.toggleLike(recipe, currentUserId)
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error("Failed to toggle like: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }
}