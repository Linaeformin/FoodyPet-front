package com.example.foodypet.home.model

data class PetPagerItem(
    val name: String,
    val imgUrl: String?,

    val mealTime: String?,
    val mealContent: String?,

    val medicineText: String,
    val waterText: String,
    val snackText: String,
    val diaryMealText: String
)