package com.example.cookup.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookup.R
import com.example.cookup.room.view_models.FeedViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: FeedViewModel by viewModels()
    private lateinit var adapter: RecipeFeedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = RecipeFeedAdapter()
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.recipes.collect { recipes ->
                adapter.submitList(recipes)
            }
        }
    }

}
