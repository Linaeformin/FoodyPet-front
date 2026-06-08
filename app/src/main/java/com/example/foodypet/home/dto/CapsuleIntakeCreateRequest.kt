package com.example.foodypet.home.dto

data class CapsuleIntakeCreateRequest(
    val capsuleIntakes: List<CapsuleIntakeCreateItem>
)

data class CapsuleIntakeCreateItem(
    val petCapsuleId: Long,
    val givenCount: Int
)