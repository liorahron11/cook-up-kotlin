package com.example.cookup.ui.home

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cookup.R
import com.example.cookup.room.view_models.FeedViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: FeedViewModel by viewModels()
    private val adapter = RecipeFeedAdapter (
        onRecipeClick = { recipe ->
            val action = HomeFragmentDirections.actionHomeFragmentToRecipeDetailsFragment(recipe)
            findNavController().navigate(action)
        },
        onUserClick = { user ->
            user?.let {
                val bundle = bundleOf("user" to user)
                findNavController().navigate(R.id.profileFragment, bundle)
            }
        },
        onLikeClick =  { recipe ->
            viewModel.toggleLike(recipe)
        }
    )
    private lateinit var progressBar: LinearLayout
    private lateinit var recycler: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar = view.findViewById<LinearLayout>(R.id.progressBar)
        recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        setLoading(true)
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

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isRefreshing.collect { refreshing ->
                swipeRefreshLayout.isRefreshing = refreshing
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
