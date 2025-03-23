package com.example.cookup.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cookup.models.RecipeWithUser
import com.example.cookup.R
import com.example.cookup.models.Recipe
import com.example.cookup.models.User
import com.google.firebase.auth.FirebaseAuth

class RecipeFeedAdapter(
    private val onRecipeClick: (Recipe) -> Unit,
    private val onUserClick: (User?) -> Unit,
    private val onLikeClick: (Recipe) -> Unit
) : ListAdapter<RecipeWithUser, RecipeFeedAdapter.RecipeViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_feed_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: RecipeWithUser) = with(itemView) {
            val recipe = data.recipe
            val user = data.user

            findViewById<TextView>(R.id.title).text = recipe.title
            findViewById<TextView>(R.id.description).text = recipe.description
            findViewById<TextView>(R.id.username).text = user?.username ?: "Unknown"

            Glide.with(this).load(user?.profileImageUrl)
                .placeholder(R.drawable.profile_image_placeholder)
                .into(findViewById<ImageView>(R.id.userAvatar))
            Glide.with(this).load(recipe.image)
                .placeholder(R.drawable.default_recipe)
                .into(findViewById<ImageView>(R.id.recipeImageView))

            if (user?.uid == "Spoonacular") {
                itemView.findViewById<LinearLayout>(R.id.likeContainer).visibility = View.GONE
            }

            itemView.findViewById<View>(R.id.username).setOnClickListener {
                onUserClick(user)
            }
            itemView.findViewById<View>(R.id.userAvatar).setOnClickListener {
                onUserClick(user)
            }
            itemView.findViewById<View>(R.id.itemLayout).setOnClickListener {
                onRecipeClick(recipe)
            }
            itemView.findViewById<ImageView>(R.id.likeButton).apply {
                val likeCount = recipe.likes.size
                val liked = recipe.likes.contains(FirebaseAuth.getInstance().currentUser?.uid)

                itemView.findViewById<ImageView>(R.id.likeButton).setImageResource(if (liked) R.drawable.ic_like_full else R.drawable.ic_like_outline)
                itemView.findViewById<TextView>(R.id.likeCount).text = likeCount.toString()
                setOnClickListener {
                    onLikeClick(recipe)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecipeWithUser>() {
            override fun areItemsTheSame(
                oldItem: RecipeWithUser,
                newItem: RecipeWithUser
            ): Boolean = oldItem.recipe.id == newItem.recipe.id

            override fun areContentsTheSame(
                oldItem: RecipeWithUser,
                newItem: RecipeWithUser
            ): Boolean = oldItem == newItem
        }
    }
}
