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
import com.example.cookup.R
import com.example.cookup.auth.AuthViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var authViewModel: AuthViewModel

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
        val imageUrl = profileImageUrl
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.profile_image_placeholder)
                .into(profileImageView!!)
        }
    }
}
