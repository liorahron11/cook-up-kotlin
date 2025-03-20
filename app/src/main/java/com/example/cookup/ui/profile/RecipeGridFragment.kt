package com.example.cookup.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookup.R
import com.example.cookup.models.Comment
import com.example.cookup.models.Ingredient
import com.example.cookup.models.Recipe
import com.example.cookup.room.entities.RecipeEntity
import com.example.cookup.room.view_models.RecipeViewModel
import com.example.cookup.services.FirestoreService
import com.example.cookup.view_models.AuthViewModel
import com.google.firebase.Timestamp

class RecipeGridFragment : Fragment() {
    private val gson = Gson()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noRecipes: RelativeLayout
    private lateinit var authViewModel: AuthViewModel
    private lateinit var recipeViewModel: RecipeViewModel
    private var adapter: RecipeAdapter? = null
    private val firestoreService = FirestoreService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recipe_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noRecipes = view.findViewById(R.id.noRecipes)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recipeRecyclerView)
        recyclerView = view.findViewById(R.id.recipeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.setHasFixedSize(true)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        setLoading(true)

        recipeViewModel.getRecipesBySenderId(authViewModel.user.value?.uid.toString()) { recipes ->
            if (recipes.isNotEmpty()) {
                adapter = RecipeAdapter(parseCachedRecipes(recipes)) { recipe ->
                    navigateToRecipeDetails(recipe)
                }
                recyclerView.adapter = adapter
                setLoading(false)
            } else {
                fetchDataFromFirestore()
            }
        }
    }

    private fun setLoading(value: Boolean) {
        if (value) {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

        }
    }

    private fun showNoRecipesMessage(value: Boolean) {
        if (value) {
            val margin = dpToPx(60, requireContext())

            val params = noRecipes.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, margin, 0, 0)
            noRecipes.layoutParams = params
            noRecipes.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            val params = noRecipes.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, 0, 0, 0)
            noRecipes.layoutParams = params
            noRecipes.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

        }
    }

    fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun navigateToRecipeDetails(recipe: Recipe) {
        val action = ProfileFragmentDirections.actionProfileFragmentToRecipeDetailsFragment(recipe)
        findNavController().navigate(action)
    }

    private fun cacheRecipes(recipes: List<Recipe>) {
        for (recipe in recipes) {
            var recipeToCache = RecipeEntity(
                id = recipe.id,
                timestamp = recipe.timestamp.toDate().time,
                senderId = recipe.senderId,
                title = recipe.title,
                description = recipe.description,
                instructions = recipe.instructions,
                ingredients = Gson().toJson(recipe.ingredients),
                comments = Gson().toJson(recipe.comments),
                likes = Gson().toJson(recipe.likes),
                image = recipe.image
            )

            recipeViewModel.insertRecipe(recipeToCache)
        }
    }

    private fun parseCachedRecipes(recipes: List<RecipeEntity>): List<Recipe> {
        val recipesToReturn = mutableListOf<Recipe>()

        for (recipe in recipes) {
            recipesToReturn.add(
                Recipe(
                    id = recipe.id,
                    timestamp = Timestamp(recipe.timestamp / 1000, (recipe.timestamp % 1000).toInt()),
                    senderId = recipe.senderId,
                    title = recipe.title,
                    description = recipe.description,
                    instructions = recipe.instructions,
                    ingredients = gson.fromJson(recipe.ingredients, Array<Ingredient>::class.java).toList(),
                    comments = gson.fromJson(recipe.comments, Array<Comment>::class.java).toList(),
                    likes = gson.fromJson(recipe.likes, Array<String>::class.java).toList(),
                    image = recipe.image
                )
            )
        }

        return recipesToReturn
    }

    private fun fetchDataFromFirestore() {
        firestoreService.getUserRecipe(
            authViewModel.user.value?.uid.toString(), onSuccess = { recipes ->
                if (recipes.isEmpty()) {
                    showNoRecipesMessage(true)
                } else {
                    cacheRecipes(recipes)

                    adapter = RecipeAdapter(recipes) { recipe ->
                        navigateToRecipeDetails(recipe)
                    }
                    recyclerView.adapter = adapter
                    showNoRecipesMessage(false)
                }

                setLoading(false)
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "שגיאה", Toast.LENGTH_SHORT).show()
                setLoading(false)
            }
        )
    }
}
