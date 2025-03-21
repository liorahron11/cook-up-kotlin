package com.example.cookup.room.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookup.models.Recipe
import com.example.cookup.models.RecipeWithUser
import com.example.cookup.room.RecipeRemoteDataSource
import com.example.cookup.room.repositories.RecipeRepository
import com.example.cookup.utils.toEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {
    private val remoteDataSource = RecipeRemoteDataSource()
    private val repository = RecipeRepository(application, RecipeRemoteDataSource())
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    val recipeFeed: StateFlow<List<RecipeWithUser>> = repository.recipesWithUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.refreshRecipesFromRemote()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.refreshRecipesFromRemote()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
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
