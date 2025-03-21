package com.example.cookup.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookup.R
import com.example.cookup.models.Recipe

class RecipeGridFragment : Fragment() {
    private val gson = Gson()
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipes: List<Recipe>
    private var adapter: RecipeGridAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recipe_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recipeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.setHasFixedSize(true)

        arguments?.getString("recipesJson")?.let { json ->
            recipes = gson.fromJson(json, Array<Recipe>::class.java).toList()
            if (recipes.isNotEmpty()) {
                adapter = RecipeGridAdapter(recipes) { recipe ->
                    navigateToRecipeDetails(recipe)
                }
                recyclerView.adapter = adapter
            }
        }
    }

    fun navigateToRecipeDetails(recipe: Recipe) {
        val action = ProfileFragmentDirections.actionProfileFragmentToRecipeDetailsFragment(recipe)
        findNavController().navigate(action)
    }
}
