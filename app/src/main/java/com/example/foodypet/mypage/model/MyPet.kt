package com.example.foodypet.mypage.model

data class MyPet(
    val name: String,
    val birth: String,
    val type: String,
    val breed: String,
    val isNeutered: Boolean,
    val gender: PetGender,
    val imageResId: Int,
    val feedCount: String,
    val feedTime: String,
    val supplement: String
)

enum class PetGender {
    GIRL,
    BOY
}