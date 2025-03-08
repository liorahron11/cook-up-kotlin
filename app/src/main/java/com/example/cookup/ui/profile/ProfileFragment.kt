package com.example.cookup.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.cookup.R
import com.example.cookup.auth.AuthViewModel
import com.example.cookup.ui.login.LoginActivity

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)
        val usernameTextView = view.findViewById<TextView>(R.id.username)
        val logoutButton = view.findViewById<Button>(R.id.btnLogout)

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

        logoutButton.setOnClickListener {
            authViewModel.logout()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
