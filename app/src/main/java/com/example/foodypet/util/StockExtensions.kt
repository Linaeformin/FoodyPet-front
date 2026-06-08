package com.example.foodypet.util

import com.example.foodypet.stock.dto.FoodUnit
import java.text.DecimalFormat

private const val IMAGE_BASE_URL = "http://15.135.188.209:8080"

fun String?.toFullImageUrl(): String? {
    if (this.isNullOrBlank()) return null

    return if (this.startsWith("http")) {
        this
    } else {
        "$IMAGE_BASE_URL$this"
    }
}

fun FoodUnit.toKoreanText(): String {
    return when (this) {
        FoodUnit.GRAM -> "g"
        FoodUnit.ML -> "ml"
        FoodUnit.COUNT -> "개"
    }
}

fun Double.toStockQuantityText(): String {
    val formatter = DecimalFormat("#.##")
    return formatter.format(this)
}

fun Double.toStockQuantityWithUnitText(unit: FoodUnit): String {
    return "${this.toStockQuantityText()}${unit.toKoreanText()}"
}