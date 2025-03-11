package com.example.cookup.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.cookup.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)
        val usernameTextView = view.findViewById<TextView>(R.id.username)
        val settingButton = view.findViewById<Button>(R.id.settingButton)

        val sharedPref = requireContext().getSharedPreferences("CookUpPrefs", Context.MODE_PRIVATE)
        val loggedUsername = sharedPref.getString("username", "").toString()
        val profileImageUrl = sharedPref.getString("profileImageUrl", "").toString()

        usernameTextView.text = loggedUsername
        val imageUrl = profileImageUrl
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.profile_image_placeholder)
                .into(profileImageView)
        }

        settingButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ProfileSettingsFragment)
        }
    }
}
