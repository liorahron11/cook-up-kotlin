package com.example.cookup.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cookup.MainActivity
import com.example.cookup.R
import com.example.cookup.view_models.AuthViewModel
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailField = view.findViewById<TextInputLayout>(R.id.emailInput).editText
        val passwordField = view.findViewById<TextInputLayout>(R.id.passwordInput).editText
        val loginButton = view.findViewById<View>(R.id.btnLogin)
        val signUpText = view.findViewById<View>(R.id.tvSignUp)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val loginCard = view.findViewById<View>(R.id.loginCard)

        loginButton.setOnClickListener {
            val email = emailField?.text.toString().trim()
            val password = passwordField?.text.toString().trim()
            authViewModel.login(email, password)
        }

        signUpText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        authViewModel.loginStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        authViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                loginCard.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                loginCard.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }
}
