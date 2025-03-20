package com.example.cookup.ui.create_recipe

import android.R.attr.height
import android.R.attr.width
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
import com.bumptech.glide.Glide
import com.example.cookup.R
import com.example.cookup.databinding.FragmentCreateRecipeBinding
import com.example.cookup.models.Recipe
import com.example.cookup.ui.shared.IngredientInputFragment
import com.example.cookup.view_models.RecipeViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class CreateRecipeFragment : Fragment(R.layout.fragment_create_recipe) {
    private lateinit var binding: FragmentCreateRecipeBinding
    private val viewModel: RecipeViewModel by viewModels()
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

        if (title.isEmpty() || description.isEmpty() || instructions.isEmpty() || imageUri == null) {
            Toast.makeText(context, "יש למלא את כל השדות ולבחור תמונה", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        viewModel.uploadImage(imageUri!!, recipeId) { imageUrl ->
            if (imageUrl != null) {
                val recipe = Recipe(
                    id = recipeId,
                    timestamp = Timestamp.now(),
                    senderId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    title = title,
                    description = description,
                    instructions = instructions,
                    comments = emptyList(),
                    likes = emptyList(),
                    image = imageUrl
                )

                viewModel.addRecipe(recipe) { success ->
                    setLoading(false)
                    if (success) {
                        Toast.makeText(context, "המתכון נוצר בהצלחה", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "שגיאה ביצירת מתכון", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                setLoading(false)
                Toast.makeText(context, "שגיאה בהעלאת תמונה", Toast.LENGTH_SHORT).show()
            }
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
}


//TODO: ADD INGREDIENTS, DESIGN BETTER