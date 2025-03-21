package com.example.cookup.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cookup.models.Recipe
import com.example.cookup.R

class RecipeFeedAdapter : ListAdapter<Recipe, RecipeFeedAdapter.RecipeViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_feed_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(recipe: Recipe) = with(itemView) {
            findViewById<TextView>(R.id.title).text = recipe.title
            findViewById<TextView>(R.id.description).text = recipe.description
            Glide.with(this).load(recipe.image).into(findViewById(R.id.imageView))
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(old: Recipe, new: Recipe) = old.id == new.id
            override fun areContentsTheSame(old: Recipe, new: Recipe) = old == new
        }
    }
}
