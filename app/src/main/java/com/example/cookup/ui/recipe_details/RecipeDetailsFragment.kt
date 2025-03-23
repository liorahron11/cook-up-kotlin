package com.example.cookup.ui.recipe_details

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.cookup.R
import com.example.cookup.models.Recipe
import com.example.cookup.room.view_models.RecipeViewModel
import com.example.cookup.view_models.AuthViewModel

class RecipeDetailsFragment : Fragment() {

    private val args: RecipeDetailsFragmentArgs by navArgs()
    private val viewModel: RecipeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe: Recipe = args.recipe
        val recipeImageView = view.findViewById<ImageView>(R.id.recipeImageView)
        val recipeTitleTextView = view.findViewById<TextView>(R.id.recipeTitleTextView)
        val recipeDescriptionTextView = view.findViewById<TextView>(R.id.recipeDescriptionTextView)
        val recipeIngredientsTextView = view.findViewById<TextView>(R.id.recipeIngredientsTextView)
        val recipeInstructionsTextView = view.findViewById<TextView>(R.id.recipeInstructionsTextView)
        val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)
        val editButton = view.findViewById<ImageButton>(R.id.editButton)

        recipeTitleTextView.text = recipe.title
        recipeDescriptionTextView.text = recipe.description
        recipeIngredientsTextView.text = recipe.ingredients.joinToString("\n") { "• ${it.name} (${it.quantity} ${it.unit?.hebrew})" }
        recipeInstructionsTextView.text = recipe.instructions

        Glide.with(requireContext())
            .load(recipe.image)
            .placeholder(R.drawable.default_recipe)
            .into(recipeImageView)

        // Check if current user is the recipe owner
        val currentUserId = authViewModel.getLoggedInUser()
        val isOwner = currentUserId != null && currentUserId == recipe.senderId

        // Only show edit/delete buttons for the recipe owner
        if (isOwner) {
            deleteButton.visibility = View.VISIBLE
            editButton.visibility = View.VISIBLE

            deleteButton.setOnClickListener {
                confirmDelete(recipe)
            }

            editButton.setOnClickListener {
                navigateToEditRecipe(recipe)
            }
        } else {
            deleteButton.visibility = View.GONE
            editButton.visibility = View.GONE
        }
    }

    private fun confirmDelete(recipe: Recipe) {
        AlertDialog.Builder(requireContext())
            .setTitle("מחיקת מתכון")
            .setMessage("האם אתה בטוח שברצונך למחוק את ${recipe.title}?")
            .setPositiveButton("מחק") { _, _ ->
                deleteRecipe(recipe.id)
            }
            .setNegativeButton("ביטול", null)
            .show()
    }

    private fun deleteRecipe(recipeId: String) {
        Toast.makeText(requireContext(), "מוחק מתכון...", Toast.LENGTH_SHORT).show()

        viewModel.deleteRecipeById(recipeId) { success, _ ->
            if (success) {
                Toast.makeText(requireContext(), "המתכון נמחק בהצלחה", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "שגיאה במחיקת המתכון", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToEditRecipe(recipe: Recipe) {
        try {
            val action = RecipeDetailsFragmentDirections.actionRecipeDetailsToEditRecipe(recipe)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "שגיאת ניווט", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}