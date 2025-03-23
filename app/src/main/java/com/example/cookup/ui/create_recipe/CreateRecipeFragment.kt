package com.example.cookup.ui.create_recipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.cookup.R
import com.example.cookup.databinding.FragmentCreateRecipeBinding
import com.example.cookup.models.Ingredient
import com.example.cookup.models.Recipe
import com.example.cookup.room.view_models.RecipeViewModel
import com.example.cookup.ui.shared.IngredientInputFragment
import com.example.cookup.view_models.CreateRecipeViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class CreateRecipeFragment : Fragment(R.layout.fragment_create_recipe) {
    private lateinit var binding: FragmentCreateRecipeBinding
    private val createRecipeViewModel: CreateRecipeViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()
    private var imageUri: Uri? = null
    private var ingredientCount = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(R.id.ingredientUnitFragmentContainer, IngredientInputFragment())
            .commit()

        binding.btnAddIngredient.setOnClickListener {
            addIngredientFragment()
        }

        binding.btnUploadImage.setOnClickListener { selectImage() }
        binding.btnSaveRecipe.setOnClickListener { saveRecipe() }
        Glide.with(binding.imageViewRecipe)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.imageViewRecipe.setImageURI(imageUri)
        }
    }

    private fun saveRecipe() {
        val title = binding.recipeTitle.text.toString().trim()
        val description = binding.recipeDescription.text.toString().trim()
        val instructions = binding.recipeInstructions.text.toString().trim()
        val recipeId = UUID.randomUUID().toString()
        val ingredients = collectIngredientsData()
        if (title.isEmpty() || description.isEmpty() || instructions.isEmpty() || ingredients.isEmpty()) {
            Toast.makeText(context, "יש למלא את כל השדות", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        if (imageUri != null) {
            createRecipeViewModel.uploadImage(imageUri!!, recipeId) { imageUrl ->
                if (imageUrl != null) {
                    val recipe = Recipe(
                        id = recipeId,
                        timestamp = Timestamp.now(),
                        senderId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        title = title,
                        description = description,
                        instructions = instructions,
                        ingredients = ingredients,
                        likes = emptyList(),
                        image = imageUrl
                    )

                    uploadRecipe(recipe)
                    findNavController().navigateUp()
                } else {
                    setLoading(false)
                    Toast.makeText(context, "שגיאה בהעלאת התמונה", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val recipe = Recipe(
                id = recipeId,
                timestamp = Timestamp.now(),
                senderId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                title = title,
                description = description,
                instructions = instructions,
                ingredients = ingredients,
                likes = emptyList(),
                image = ""
            )

            uploadRecipe(recipe)
        }
    }

    private fun addIngredientFragment() {
        val newFragment = IngredientInputFragment.newInstance(ingredientCount++)

        childFragmentManager.beginTransaction()
            .add(R.id.ingredientUnitFragmentContainer, newFragment, "ingredient_$ingredientCount")
            .commit()
    }

    private fun setLoading(value: Boolean) {
        if (value) {
            binding.progressBar.visibility = View.VISIBLE
            binding.formLayout.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.formLayout.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }

    private fun collectIngredientsData(): List<Ingredient> {
        val ingredientList = mutableListOf<Ingredient>()

        for (fragment in childFragmentManager.fragments) {
            if (fragment is IngredientInputFragment) {
                val ingredient = fragment.getIngredientData()
                if (ingredient != null) {
                    ingredientList.add(ingredient)
                }
            }
        }

        return ingredientList
    }

    private fun uploadRecipe(recipe: Recipe) {
        createRecipeViewModel.addRecipe(recipe) { success ->
            setLoading(false)
            if (success) {
                Toast.makeText(context, "המתכון נוצר בהצלחה", Toast.LENGTH_SHORT).show()
                recipeViewModel.deleteRecipesByUser(recipe.senderId)
                findNavController().navigate(R.id.action_createRecipeFragment_to_profileFragment)
            } else {
                Toast.makeText(context, "שגיאה ביצירת מתכון", Toast.LENGTH_SHORT).show()
            }
        }
    }
}