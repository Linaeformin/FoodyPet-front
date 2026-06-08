package com.example.foodypet.home.dto

data class DietAnalysisResponse(
    val calciumPhosphorus: CalciumPhosphorusResponse,
    val canConfirm: Boolean,
    val confirmed: Boolean,
    val dailyDietId: Long,
    val dietDate: String,
    val nutrientBars: List<NutrientBarResponse>,
    val nutrientRatios: List<NutrientRatioResponse>,
    val petId: Long,
    val petName: String,
    val taurine: TaurineResponse,
    val title: String
)

data class CalciumPhosphorusResponse(
    val calcium: Double,
    val displayRatio: String,
    val phosphorus: Double,
    val ratio: Double,
    val status: String,
    val statusText: String
)

data class NutrientBarResponse(
    val displayValue: String,
    val name: String,
    val percent: Double,
    val status: String,
    val statusText: String,
    val unit: String,
    val value: Double
)

data class NutrientRatioResponse(
    val displayText: String,
    val name: String,
    val unit: String,
    val value: Double
)

data class TaurineResponse(
    val displayValue: String,
    val status: String,
    val statusText: String,
    val unit: String,
    val value: Double
)