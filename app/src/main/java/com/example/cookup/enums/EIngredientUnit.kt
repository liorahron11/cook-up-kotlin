package com.example.cookup.enums

enum class EIngredientUnit(val hebrew: String) {
    GRAM("גרם"),
    KILOGRAM("קילוגרם"),
    MILLILITER("מיליליטר"),
    LITER("ליטר"),
    TEASPOON("כפית"),
    TABLESPOON("כף"),
    CUP("כוס"),
    PIECE("יחידה"),
    PINCH("קורט");

    override fun toString(): String {
        return hebrew
    }
}
