package com.example.cookup.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cookup.MainActivity
import com.example.cookup.R
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var viewModel: LoginViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val emailInputLayout = view.findViewById<TextInputLayout>(R.id.emailInput)
        val passwordInputLayout = view.findViewById<TextInputLayout>(R.id.passwordInput)
        val loginButton = view.findViewById<Button>(R.id.btnLogin)

        val emailField = emailInputLayout.editText
        val passwordField = passwordInputLayout.editText

        emailField?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onEmailChanged(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        passwordField?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onPasswordChanged(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loginButton.setOnClickListener {
            viewModel.login()
        }

        viewModel.loginStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                val sharedPref = requireActivity().getSharedPreferences("CookUpPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", true)
                    apply()
                }

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "שם משתמש או סיסמה אינם נכונים", Toast.LENGTH_SHORT).show()
            }
        }


        view.findViewById<TextView>(R.id.tvSignUp).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }
}

