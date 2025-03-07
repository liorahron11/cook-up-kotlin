package com.example.cookup.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cookup.MainActivity
import com.example.cookup.R
import com.example.cookup.auth.AuthViewModel
import com.google.android.material.textfield.TextInputLayout

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInputLayout = view.findViewById<TextInputLayout>(R.id.emailInput)
        val passwordInputLayout = view.findViewById<TextInputLayout>(R.id.passwordInput)
        val confirmPasswordInputLayout = view.findViewById<TextInputLayout>(R.id.confirmPasswordInput)
        val signupButton = view.findViewById<Button>(R.id.btnSignup)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val signupCard = view.findViewById<View>(R.id.signupCard)


        val emailField = emailInputLayout.editText
        val passwordField = passwordInputLayout.editText
        val confirmPasswordField = confirmPasswordInputLayout.editText

        signupButton.setOnClickListener {
            val email = emailField?.text.toString().trim()
            val password = passwordField?.text.toString().trim()
            val confirmPassword = confirmPasswordField?.text.toString().trim()

            if (confirmPassword == password) {
                authViewModel.signup(email, password)
            } else {
                authViewModel.setAuthErrorMessage("סיסמה ואישור סיסמה אינם תואמים")
            }
        }

        authViewModel.signupStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "נרשמת בהצלחה", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "יש למלא את כל הפרטים", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                signupCard.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                signupCard.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }

        view.findViewById<TextView>(R.id.tvLogin).setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }
}

