package com.example.cookup.enums

enum class EIngredientUnit(val hebrew: String) {
    GRAM("גרם"),
    KILOGRAM("קילוגרם"),
    POUND("קילוגרם"),
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

    companion object {
        fun fromSpoonacularUnit(unit: String?): EIngredientUnit {
            return when (unit?.lowercase()) {
                "g", "gram", "grams" -> GRAM
                "kg", "kilogram", "kilograms" -> KILOGRAM
                "oz", "ounce", "ounces", "lb", "pound", "pounds" -> POUND
                "ml", "milliliter", "milliliters" -> MILLILITER
                "l", "liter", "liters" -> LITER
                "tsp", "teaspoon", "teaspoons" -> TEASPOON
                "tbsp", "tablespoon", "tablespoons" -> TABLESPOON
                "cup", "cups" -> CUP
                "pinch", "pinches" -> PINCH
                "piece", "pieces", "unit", "units" -> PIECE
                else -> PIECE
            }
        }
    }
}
