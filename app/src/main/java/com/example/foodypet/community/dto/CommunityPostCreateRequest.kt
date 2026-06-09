package com.example.foodypet.community.dto

data class CommunityPostCreateRequest(
    val title: String,
    val content: String,
    val mealDiaryId: Long
)