package com.example.foodypet.stock.model

import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.enum.StockSourceType

data class StockItem(
    val foodId: Long? = null,
    val expireDate: String = "",
    val name: String,
    val count: String,
    val category: StockCategory,
    val isExpired: Boolean = false,
    val createdAt: String = "",
    val sourceType: StockSourceType,
    val imageUrl: String? = null,
    val nutritionImageUrl: String? = null,
    val isSelected: Boolean = false
)