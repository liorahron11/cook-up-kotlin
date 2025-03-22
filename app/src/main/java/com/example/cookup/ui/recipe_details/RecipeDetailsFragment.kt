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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.cookup.R
import com.example.cookup.models.Recipe
import com.example.cookup.room.view_models.RecipeViewModel

class RecipeDetailsFragment : Fragment() {

    private val args: RecipeDetailsFragmentArgs by navArgs()
    private lateinit var viewModel: RecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application))
            .get(RecipeViewModel::class.java)

        val recipe: Recipe = args.recipe
        val recipeImageView = view.findViewById<ImageView>(R.id.recipeImageView)
        val recipeTitleTextView = view.findViewById<TextView>(R.id.recipeTitleTextView)
        val recipeDescriptionTextView = view.findViewById<TextView>(R.id.recipeDescriptionTextView)
        val recipeIngredientsTextView = view.findViewById<TextView>(R.id.recipeIngredientsTextView)
        val recipeInstructionsTextView = view.findViewById<TextView>(R.id.recipeInstructionsTextView)
        val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)

        recipeTitleTextView.text = recipe.title
        recipeDescriptionTextView.text = recipe.description
        recipeIngredientsTextView.text = recipe.ingredients.joinToString("\n") { "• ${it.name} (${it.quantity} ${it.unit})" }
        recipeInstructionsTextView.text = recipe.instructions

        Glide.with(requireContext())
            .load(recipe.image)
            .placeholder(R.drawable.default_recipe)
            .into(recipeImageView)

        deleteButton.setOnClickListener {
            confirmDelete(recipe)
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

        viewModel.deleteRecipeById(recipeId) { success, deletedCount ->
            if (success) {
                Toast.makeText(requireContext(), "המתכון נמחק בהצלחה ($deletedCount)", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "שגיאה במחיקת המתכון", Toast.LENGTH_LONG).show()
            }
        }
    }
}