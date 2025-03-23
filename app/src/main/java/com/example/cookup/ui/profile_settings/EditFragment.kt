package com.example.cookup.ui.profile_settings

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.cookup.view_models.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.example.cookup.R

class EditFragment : Fragment(R.layout.fragment_edit) {

    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var editLayout: LinearLayout
    private lateinit var buttonsLayout: LinearLayout
    private var field: String? = null
    private var fieldHebrewName: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        field = arguments?.getString("field")
        fieldHebrewName = arguments?.getString("fieldHebrewName")

        val inputField = view.findViewById<TextInputEditText>(R.id.editTextInput)
        inputField.hint = fieldHebrewName
        if (field == "Email") {
            inputField.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        } else if (field === "Password") {
            inputField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val editTitle = view.findViewById<TextView>(R.id.editTitle)
        editTitle.text = getString(R.string.edit_title, fieldHebrewName)

        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val newValue = inputField.text.toString().trim()

            if (newValue.isEmpty()) {
                inputField.error = " נדרש למלא $fieldHebrewName"
                showErrorToast(" נדרש למלא $fieldHebrewName")
                return@setOnClickListener
            }

            if (field == "Email" && !isValidEmail(newValue)) {
                inputField.error = "אימייל אינו תקין"
                showErrorToast("הזן כתובת מייל תקינה")
                return@setOnClickListener
            }

            authViewModel.updateUserField(field!!, newValue)
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            navController.popBackStack()
        }

        authViewModel.updateStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                navController.popBackStack()
                Toast.makeText(requireContext(), "$fieldHebrewName עודכן ", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                showErrorToast(it)
            }
        }

        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        editLayout = view.findViewById<LinearLayout>(R.id.editLayout)
        buttonsLayout = view.findViewById<LinearLayout>(R.id.buttonsLayout)

        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            setLoadind(isLoading)
        }
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(requireContext(), " שגיאה: $message", Toast.LENGTH_LONG).show()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setLoadind(value: Boolean) {
        if (value) {
            editLayout.visibility = View.GONE
            buttonsLayout.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            editLayout.visibility = View.VISIBLE
            buttonsLayout.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}
