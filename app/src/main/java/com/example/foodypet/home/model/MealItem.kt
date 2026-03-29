package com.example.foodypet.home.model

data class MealItem(
    val mealId: Long,
    val time: String,
    val content: String,
    val isFed: Boolean
)