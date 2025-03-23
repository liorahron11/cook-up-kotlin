package com.example.cookup.view_models

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cookup.models.Ingredient
import com.example.cookup.models.Recipe
import com.example.cookup.room.entities.RecipeEntity
import com.example.cookup.room.view_models.RecipeViewModel
import com.google.firebase.Timestamp
import com.google.gson.Gson
import kotlinx.coroutines.launch

class EditRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val createRecipeViewModel = CreateRecipeViewModel()
    private val recipeViewModel = RecipeViewModel(application)

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _successMessage = MutableLiveData<String?>(null)
    val successMessage: LiveData<String?> = _successMessage

    private val _navigateBack = MutableLiveData<Boolean>(false)
    val navigateBack: LiveData<Boolean> = _navigateBack

    private var _currentRecipe: Recipe? = null

    fun initialize(recipe: Recipe) {
        _currentRecipe = recipe
    }

    fun uploadImageAndUpdateRecipe(
        imageUri: Uri?,
        title: String,
        description: String,
        instructions: String,
        ingredients: List<Ingredient>
    ) {
        if (!validateInput(title, instructions, ingredients)) {
            _errorMessage.value = "יש למלא את כל השדות הנדרשים"
            return
        }

        _loading.value = true

        val recipeToUpdate = _currentRecipe ?: return

        if (imageUri != null) {
            createRecipeViewModel.uploadImage(imageUri, recipeToUpdate.id) { imageUrl ->
                if (imageUrl != null) {
                    updateRecipe(recipeToUpdate, title, description, instructions, ingredients, imageUrl)
                } else {
                    _loading.value = false
                    _errorMessage.value = "שגיאה בהעלאת התמונה"
                }
            }
        } else {
            updateRecipe(recipeToUpdate, title, description, instructions, ingredients, recipeToUpdate.image)
        }
    }

    private fun updateRecipe(
        originalRecipe: Recipe,
        title: String,
        description: String,
        instructions: String,
        ingredients: List<Ingredient>,
        imageUrl: String
    ) {
        val updatedRecipe = originalRecipe.copy(
            title = title,
            description = description,
            instructions = instructions,
            ingredients = ingredients,
            timestamp = Timestamp.now(),
            image = imageUrl
        )

        createRecipeViewModel.addRecipe(updatedRecipe) { success ->
            if (success) {
                updateLocalCache(updatedRecipe)
                _successMessage.value = "המתכון עודכן בהצלחה"
                _navigateBack.value = true
            } else {
                _errorMessage.value = "שגיאה בעדכון המתכון"
                _loading.value = false
            }
        }
    }

    private fun updateLocalCache(recipe: Recipe) {
        val recipeEntity = convertToEntity(recipe)
        viewModelScope.launch {
            recipeViewModel.insertRecipe(recipeEntity)
            _loading.value = false
        }
    }

    private fun convertToEntity(recipe: Recipe): RecipeEntity {
        return RecipeEntity(
            id = recipe.id,
            timestamp = recipe.timestamp.toDate().time,
            senderId = recipe.senderId,
            title = recipe.title,
            description = recipe.description,
            instructions = recipe.instructions,
            ingredients = Gson().toJson(recipe.ingredients),
            likes = Gson().toJson(recipe.likes),
            image = recipe.image
        )
    }

    private fun validateInput(title: String, instructions: String, ingredients: List<Ingredient>): Boolean {
        return title.isNotEmpty() && instructions.isNotEmpty() && ingredients.isNotEmpty()
    }

    fun resetNavigation() {
        _navigateBack.value = false
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    fun resetSuccessMessage() {
        _successMessage.value = null
    }
}