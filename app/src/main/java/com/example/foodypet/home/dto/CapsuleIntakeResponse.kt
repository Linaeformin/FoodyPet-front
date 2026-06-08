package com.example.foodypet.home.dto

data class CapsuleIntakeResponse(
    val petId: Long,
    val capsules: List<CapsuleIntakeDto>
)

data class CapsuleIntakeDto(
    val petCapsuleId: Long,
    val capsuleName: String,
    val capsuleCount: Int,
    val givenCount: Int
)