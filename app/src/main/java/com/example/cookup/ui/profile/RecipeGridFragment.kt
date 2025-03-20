package com.example.cookup.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        recyclerView = view.findViewById(R.id.recipeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.setHasFixedSize(true)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        firestoreService.getUserRecipe(
            authViewModel.user.value?.uid.toString(), onSuccess = { recipes ->
                adapter = RecipeAdapter(recipes) { recipe ->
                    Toast.makeText(requireContext(), "Clicked: ${recipe.title}", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = adapter
                adapter?.notifyDataSetChanged()
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "שגיאה", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.adapter = adapter
    }
}
