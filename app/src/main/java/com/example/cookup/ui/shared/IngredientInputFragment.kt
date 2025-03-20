package com.example.cookup.ui.shared

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cookup.R
import com.example.cookup.enums.EIngredientUnit

class IngredientInputFragment : Fragment(R.layout.fragment_ingredient_input) {

    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            index = it.getInt("index")
        }
    }

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

        view.findViewById<Button>(R.id.btnRemoveIngredient).setOnClickListener {
            removeFragment()
        }
    }

    companion object {
        fun newInstance(index: Int): IngredientInputFragment {
            val fragment = IngredientInputFragment()
            val args = Bundle()
            args.putInt("index", index)
            fragment.arguments = args
            return fragment
        }
    }

    private fun removeFragment() {
        parentFragmentManager.beginTransaction().remove(this).commit()
    }
}
