package com.example.foodypet.home.dto

data class SnackAutocompleteResponse(
    val stockId: Long,
    val foodName: String,
    val unit: String,
    val unitLabel: String
)