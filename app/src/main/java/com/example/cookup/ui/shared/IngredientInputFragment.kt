package com.example.cookup.ui.shared

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cookup.R
import com.example.cookup.enums.EIngredientUnit

class IngredientInputFragment : Fragment(R.layout.fragment_ingredient_input) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val units = EIngredientUnit.entries.map { it.hebrew } // Get Hebrew names from enum
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, units)

        val dropdown = view.findViewById<AutoCompleteTextView>(R.id.ingredientMenu)
        dropdown.setAdapter(adapter)
        dropdown.setOnClickListener {
            dropdown.showDropDown()
        }
        // Handle selection
        dropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedUnit = EIngredientUnit.entries[position]
            Toast.makeText(context, "Selected: ${selectedUnit.hebrew}", Toast.LENGTH_SHORT).show()
        }
    }

}
