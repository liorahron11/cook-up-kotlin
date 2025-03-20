package com.example.cookup.ui.recipe_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.cookup.R
import com.example.cookup.models.Recipe

class RecipeDetailsFragment : Fragment() {

    private val args: RecipeDetailsFragmentArgs by navArgs()

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

        recipeTitleTextView.text = recipe.title
        recipeDescriptionTextView.text = recipe.description
        recipeIngredientsTextView.text = recipe.ingredients.joinToString("\n") { "• ${it.name} (${it.quantity} ${it.unit})" }
        recipeInstructionsTextView.text = recipe.instructions

        Glide.with(requireContext())
            .load(recipe.image)
            .placeholder(R.drawable.default_recipe)
            .into(recipeImageView)
    }
}
