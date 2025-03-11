package com.example.cookup.ui.profile_settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cookup.R
import com.example.cookup.auth.AuthViewModel
import com.example.cookup.ui.login.LoginActivity

class ProfileSettingsFragment : Fragment(R.layout.fragment_profile_settings) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutButton = view.findViewById<Button>(R.id.btnLogout)

        logoutButton.setOnClickListener {
            authViewModel.logout()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
