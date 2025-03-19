package com.example.cookup.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.example.cookup.R
import com.example.cookup.auth.AuthViewModel
import java.util.concurrent.Executors

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var authViewModel: AuthViewModel
    private var cachedImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = requireContext().getSharedPreferences("CookUpPrefs", Context.MODE_PRIVATE)
        val profileImageUrl = sharedPref.getString("profileImageUrl", "").toString()

        Executors.newSingleThreadExecutor().execute {
            val future = Glide.with(requireContext())
                .downloadOnly()
                .load(profileImageUrl)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        setUserDetails()

        val settingButton = view.findViewById<Button>(R.id.settingButton)
        settingButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ProfileSettingsFragment)
        }

        authViewModel.updateStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                setUserDetails()
            }
        }
    }

    private fun setUserDetails() {
        val usernameTextView = view?.findViewById<TextView>(R.id.username)
        val profileImageView = view?.findViewById<ImageView>(R.id.profileImageView)
        val sharedPref = requireContext().getSharedPreferences("CookUpPrefs", Context.MODE_PRIVATE)
        val loggedUsername = sharedPref.getString("username", "").toString()
        val profileImageUrl = sharedPref.getString("profileImageUrl", "").toString()

        usernameTextView?.text = loggedUsername
        if (profileImageUrl.isNotEmpty()) {
            cachedImagePath?.let {
                Glide.with(this)
                    .load(it)
                    .into(profileImageView!!)
            } ?: run {
                Glide.with(this)
                    .load(profileImageUrl)
                    .into(profileImageView!!)
            }
        }
    }
}
