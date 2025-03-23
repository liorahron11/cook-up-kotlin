package com.example.cookup.ui.recipe_details

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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.cookup.R
import com.example.cookup.databinding.FragmentCreateRecipeBinding
import com.example.cookup.models.Ingredient
import com.example.cookup.models.Recipe
import com.example.cookup.ui.shared.IngredientInputFragment
import com.example.cookup.view_models.EditRecipeViewModel

class EditRecipeFragment : Fragment(R.layout.fragment_create_recipe) {
    private lateinit var binding: FragmentCreateRecipeBinding
    private val viewModel: EditRecipeViewModel by viewModels()
    private var imageUri: Uri? = null
    private var ingredientCount = 0

    private val args: EditRecipeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initialize(args.recipe)

        setupObservers()

        clearIngredientFragments()

        populateUI(args.recipe)

        binding.btnAddIngredient.setOnClickListener {
            addIngredientFragment()
        }

        binding.btnUploadImage.setOnClickListener { selectImage() }

        binding.btnSaveRecipe.text = "עדכן מתכון"
        binding.btnSaveRecipe.setOnClickListener { updateRecipe() }
    }

    private fun setupObservers() {
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.formLayout.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.formLayout.visibility = View.VISIBLE
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.resetErrorMessage()
            }
        })

        viewModel.successMessage.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.resetSuccessMessage()
            }
        })

        viewModel.navigateBack.observe(viewLifecycleOwner, Observer { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigateUp()
                findNavController().navigateUp()
                viewModel.resetNavigation()
            }
        })
    }

    private fun clearIngredientFragments() {
        // Remove all previous ingredient fragments
        val fragmentTransaction = childFragmentManager.beginTransaction()
        childFragmentManager.fragments.forEach { fragment ->
            if (fragment is IngredientInputFragment) {
                fragmentTransaction.remove(fragment)
            }
        }
        fragmentTransaction.commit()
    }

    private fun populateUI(recipe: Recipe) {
        binding.recipeTitle.setText(recipe.title)
        binding.recipeDescription.setText(recipe.description)
        binding.recipeInstructions.setText(recipe.instructions)

        if (recipe.image.isNotEmpty()) {
            Glide.with(requireContext())
                .load(recipe.image)
                .placeholder(R.drawable.default_recipe)
                .into(binding.imageViewRecipe)
        }

        recipe.ingredients.forEachIndexed { index, ingredient ->
            addExistingIngredient(index, ingredient)
            ingredientCount = index + 1
        }

        if (recipe.ingredients.isEmpty()) {
            addIngredientFragment()
        }
    }

    private fun addExistingIngredient(index: Int, ingredient: Ingredient) {
        val fragment = IngredientInputFragment.newInstance(index)

        val args = Bundle()
        args.putString("name", ingredient.name)
        args.putString("quantity", ingredient.quantity.toString())
        args.putString("unit", ingredient.unit?.hebrew)
        fragment.arguments = args

        childFragmentManager.beginTransaction()
            .add(R.id.ingredientUnitFragmentContainer, fragment, "ingredient_$index")
            .commit()

        ingredientCount = Math.max(ingredientCount, index + 1)
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

    private fun updateRecipe() {
        val title = binding.recipeTitle.text.toString().trim()
        val description = binding.recipeDescription.text.toString().trim()
        val instructions = binding.recipeInstructions.text.toString().trim()
        val ingredients = collectIngredientsData()

        viewModel.uploadImageAndUpdateRecipe(
            imageUri,
            title,
            description,
            instructions,
            ingredients
        )
    }

    private fun addIngredientFragment() {
        val newFragment = IngredientInputFragment.newInstance(ingredientCount++)

        childFragmentManager.beginTransaction()
            .add(R.id.ingredientUnitFragmentContainer, newFragment, "ingredient_$ingredientCount")
            .commit()
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

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}