package com.example.cookup.ui.login

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookup.R

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        view.findViewById<TextView>(R.id.tvSignUp).setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }
}
