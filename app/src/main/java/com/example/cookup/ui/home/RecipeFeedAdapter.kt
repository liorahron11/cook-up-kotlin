package com.example.cookup.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cookup.models.RecipeWithUser
import com.example.cookup.R

class RecipeFeedAdapter : ListAdapter<RecipeWithUser, RecipeFeedAdapter.RecipeViewHolder>(DIFF) {

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
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<RecipeWithUser>() {
            override fun areItemsTheSame(old: RecipeWithUser, new: RecipeWithUser) = old.recipe.id == new.recipe.id
            override fun areContentsTheSame(old: RecipeWithUser, new: RecipeWithUser) = old == new
        }
    }
}
