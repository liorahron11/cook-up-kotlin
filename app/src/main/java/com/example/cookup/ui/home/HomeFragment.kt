package com.example.cookup.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookup.R
import com.example.cookup.room.view_models.FeedViewModel
import com.google.android.material.appbar.MaterialToolbar

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: FeedViewModel by viewModels()
    private lateinit var adapter: RecipeFeedAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var recycler: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        recycler = view.findViewById<RecyclerView>(R.id.recyclerView)

        setLoading(true)
        adapter = RecipeFeedAdapter()
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.recipeFeed.collect { recipes ->
                adapter.submitList(recipes)

                if (recipes.isNotEmpty()) {
                    setLoading(false)
                }
            }
        }
    }

    private fun setLoading(value: Boolean) {
        if (value) {
            progressBar.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }
    }
}
