package com.example.cookup.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookup.R
import com.example.cookup.services.FirestoreService
import com.example.cookup.view_models.AuthViewModel

class RecipeGridFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noRecipes: RelativeLayout
    private var adapter: RecipeAdapter? = null
    private lateinit var authViewModel: AuthViewModel
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
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        setLoading(true)
        firestoreService.getUserRecipe(
            authViewModel.user.value?.uid.toString(), onSuccess = { recipes ->
                if (recipes.isEmpty()) {
                    showNoRecipesMessage(true)
                } else {
                    adapter = RecipeAdapter(recipes) { recipe ->
                        Toast.makeText(requireContext(), "Clicked: ${recipe.title}", Toast.LENGTH_SHORT).show()
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

        recyclerView.adapter = adapter
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
}
