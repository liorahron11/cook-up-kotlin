package com.example.cookup.ui.signup

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
import com.example.cookup.R
import com.google.android.material.textfield.TextInputLayout

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var viewModel: SignupViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]

        val usernameInputLayout = view.findViewById<TextInputLayout>(R.id.usernameInput)
        val emailInputLayout = view.findViewById<TextInputLayout>(R.id.emailInput)
        val passwordInputLayout = view.findViewById<TextInputLayout>(R.id.passwordInput)
        val signupButton = view.findViewById<Button>(R.id.btnSignup)


        val usernameField = usernameInputLayout.editText
        val emailField = emailInputLayout.editText
        val passwordField = passwordInputLayout.editText

        usernameField?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onUsernameChanged(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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

        signupButton.setOnClickListener {
            viewModel.signUp()
        }

        viewModel.signupStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "נרשמת בהצלחה", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            } else {
                Toast.makeText(requireContext(), "יש למלא את כל הפרטים", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<TextView>(R.id.tvLogin).setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }
}

