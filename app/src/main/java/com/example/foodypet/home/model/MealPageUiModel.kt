package com.example.foodypet.home.model

data class MealPageUiModel(
    val label: String,          // "13:00" or "4회"
    val foods: List<FoodUiModel>
)

data class FoodUiModel(
    var name: String,
    var amount: String,
    var unit: String
)