package com.example.cookup.ui.shared

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.cookup.R
import com.example.cookup.enums.EIngredientUnit
import com.example.cookup.models.Ingredient
import com.google.android.material.textfield.TextInputEditText

class IngredientInputFragment : Fragment(R.layout.fragment_ingredient_input) {

    private var index: Int = 0
    private lateinit var quantity: TextInputEditText
    private lateinit var name: TextInputEditText
    private lateinit var dropdown: AutoCompleteTextView
    private var ingredientUnit: EIngredientUnit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            index = it.getInt("index", 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val units = EIngredientUnit.entries.map { it.hebrew } // Get Hebrew names from enum
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, units)

        dropdown = view.findViewById(R.id.ingredientMenu)
        dropdown.setAdapter(adapter)
        dropdown.setOnClickListener {
            dropdown.showDropDown()
        }

        // Handle selection
        dropdown.setOnItemClickListener { _, _, position, _ ->
            ingredientUnit = EIngredientUnit.entries[position]
        }

        quantity = view.findViewById(R.id.quantityInput)
        name = view.findViewById(R.id.nameInput)

        view.findViewById<Button>(R.id.btnRemoveIngredient).setOnClickListener {
            removeFragment()
        }

        arguments?.let { args ->
            val ingredientName = args.getString("name")
            val quantityStr = args.getString("quantity")
            val unitName = args.getString("unit")

            if (ingredientName != null) {
                name.setText(ingredientName)
            }

            if (quantityStr != null) {
                quantity.setText(quantityStr)
            }

            if (unitName != null) {
                EIngredientUnit.entries.find { it.hebrew == unitName }?.let { unit ->
                    ingredientUnit = unit
                    dropdown.setText(unit.hebrew, false)
                }
            }
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

    fun getIngredientData(): Ingredient? {
        val quantityText = quantity.text.toString()
        val nameText = name.text.toString()

        if (quantityText.isNotEmpty() && nameText.isNotEmpty() && ingredientUnit != null) {
            return Ingredient(
                quantityText.toDoubleOrNull() ?: 1.0,
                ingredientUnit ?: EIngredientUnit.entries.first(),
                nameText
            )
        }

        return null
    }
}