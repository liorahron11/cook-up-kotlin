package com.example.cookup.ui.profile
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.example.cookup.R
import com.example.cookup.models.Comment
import com.example.cookup.models.Ingredient
import com.example.cookup.models.Recipe
import com.example.cookup.models.User
import com.example.cookup.room.entities.Profile
import com.example.cookup.room.entities.RecipeEntity
import com.example.cookup.view_models.AuthViewModel
import com.example.cookup.room.view_models.ProfileViewModel
import com.example.cookup.room.view_models.RecipeViewModel
import com.example.cookup.services.FirestoreService
import com.google.firebase.Timestamp
import com.google.gson.Gson
import java.util.concurrent.Executors
import kotlin.getValue

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var progressBar: ProgressBar
    private lateinit var noRecipes: RelativeLayout
    private lateinit var profileLayout: LinearLayout
    private lateinit var recipesCount: TextView
    private lateinit var user: User
    private lateinit var userRecipes: List<Recipe>
    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()
    private var cachedImagePath: String? = null
    private val args: ProfileFragmentArgs by navArgs()
    private var cachedProfile: Profile? = null
    private val firestoreService = FirestoreService()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (args.user != null) {
            user = args.user!!
        } else {
            user = authViewModel.user.value!!
        }

        profileViewModel.getProfile { profile ->
            if (profile != null) {
                cachedProfile = profile
            } else {
                val profile = Profile(username = user.username, email = user.email, id = user.uid)
                profileViewModel.insertProfile(profile)
                cachedProfile = profile
            }

            Executors.newSingleThreadExecutor().execute {
                val future = Glide.with(requireContext())
                    .downloadOnly()
                    .load(cachedProfile?.profileImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)

                try {
                    val file = future.get()
                    cachedImagePath = file.absolutePath
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserDetails()

        recipesCount = view.findViewById(R.id.recipesCount)
        noRecipes = view.findViewById(R.id.noRecipes)
        progressBar = view.findViewById(R.id.progressBar)
        profileLayout = view.findViewById(R.id.profileLayout)
        val settingButton = view.findViewById<Button>(R.id.settingButton)
        settingButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ProfileSettingsFragment)
        }

        authViewModel.updateStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                setUserDetails()
            }
        }

        setLoading(true)
        recipeViewModel.getRecipesBySenderId(authViewModel.user.value?.uid.toString()) { recipes ->
            if (recipes.isNotEmpty()) {
                userRecipes = parseCachedRecipes(recipes)
                setRecipesCount()
                openRecipeGridFragment()
                setLoading(false)
            } else {
                fetchDataFromFirestore()
            }
        }
    }

    private fun setUserDetails() {
        val usernameTextView = view?.findViewById<TextView>(R.id.username)
        val profileImageView = view?.findViewById<ImageView>(R.id.profileImageView)

        usernameTextView?.text = user.username
        if (cachedProfile?.profileImageUrl?.isNotEmpty() == true) {
            cachedImagePath?.let {
                Glide.with(this)
                    .load(it)
                    .into(profileImageView!!)
            } ?: run {
                Glide.with(this)
                    .load(cachedProfile?.profileImageUrl)
                    .into(profileImageView!!)
            }
        }
    }

    private fun openRecipeGridFragment() {
        val bundle = Bundle()
        bundle.putString("recipesJson", Gson().toJson(userRecipes))

        val recipeGridFragment = RecipeGridFragment()
        recipeGridFragment.arguments = bundle

        childFragmentManager.beginTransaction()
            .replace(R.id.gridFragmentContainer, recipeGridFragment)
            .commit()
    }

    private fun fetchDataFromFirestore() {
        firestoreService.getUserRecipe(
            cachedProfile?.id.toString(), onSuccess = { recipes ->
                if (!recipes.isEmpty()) {
                    cacheRecipes(recipes)
                    openRecipeGridFragment()
                } else {
                    showNoRecipesMessage(true)
                }

                userRecipes = recipes
                setRecipesCount()
                setLoading(false)
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "שגיאה", Toast.LENGTH_SHORT).show()
                setLoading(false)
            }
        )
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

    private fun setLoading(value: Boolean) {
        if (value) {
            progressBar.visibility = View.VISIBLE
            profileLayout.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            profileLayout.visibility = View.VISIBLE
        }
    }

    private fun showNoRecipesMessage(value: Boolean) {
        if (value) {
            val margin = dpToPx(60, requireContext())

            val params = noRecipes.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, margin, 0, 0)
            noRecipes.layoutParams = params
            noRecipes.visibility = View.VISIBLE
            view?.findViewById<FrameLayout>(R.id.gridFragmentContainer)?.visibility = View.GONE
        } else {
            val params = noRecipes.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, 0, 0, 0)
            noRecipes.layoutParams = params
            noRecipes.visibility = View.GONE
            view?.findViewById<FrameLayout>(R.id.gridFragmentContainer)?.visibility = View.VISIBLE

        }
    }

    fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    private fun setRecipesCount() {
        recipesCount.text = userRecipes.size.toString() + " מתכונים"
    }
}
