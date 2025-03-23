package com.example.cookup.room.view_models

import com.example.cookup.models.RecipeWithUser

sealed class FeedUiState {
    object Loading : FeedUiState()
    data class Success(val recipes: List<RecipeWithUser>) : FeedUiState()
    data class Error(val message: String) : FeedUiState()
}