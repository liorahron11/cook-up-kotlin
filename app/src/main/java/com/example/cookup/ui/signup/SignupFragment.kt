package com.example.cookup.ui.signup

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookup.R

class SignupFragment : Fragment(R.layout.fragment_signup) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        view.findViewById<TextView>(R.id.tvLogin).setOnClickListener {
            navController.navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }
}
