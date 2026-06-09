package com.example.foodypet.home.dto

import java.math.BigDecimal

data class TreatDiaryTodayResponse(
    val treatRound: Int,
    val items: List<TreatDiaryTodayItemResponse>
)

data class TreatDiaryTodayItemResponse(
    val foodName: String,
    val amount: BigDecimal,
    val unit: String,
    val unitLabel: String
)