package com.example.cookup.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.cookup.R
import com.example.cookup.ui.login.LoginActivity

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutButton = view.findViewById<Button>(R.id.btnLogout)

        logoutButton.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("CookUpPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("isLoggedIn", false)
                apply()
            }

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}

