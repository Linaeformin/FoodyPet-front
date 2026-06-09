package com.example.foodypet.home.dto

import java.math.BigDecimal

data class PetTreatDiaryCreateRequest(
    val treatRound: Int,
    val items: List<TreatDiaryCreateItemRequest>
)

data class TreatDiaryCreateItemRequest(
    val stockId: Long,
    val amount: BigDecimal,
    val unit: String
)