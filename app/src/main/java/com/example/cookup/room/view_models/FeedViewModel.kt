package com.example.cookup.room.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookup.models.RecipeWithUser
import com.example.cookup.room.RecipeRemoteDataSource
import com.example.cookup.room.repositories.RecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecipeRepository(application, RecipeRemoteDataSource())

    val recipeFeed: StateFlow<List<RecipeWithUser>> = repository.recipesWithUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.refreshRecipesFromRemote()
        }
    }
}
