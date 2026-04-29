package com.example.foodypet.stock.model

import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.enum.StockSourceType

data class StockItem(
    val expireDate: String,
    val name: String,
    val count: String,
    val category: StockCategory,
    val isExpired: Boolean,
    val createdAt: String,
    val sourceType: StockSourceType
)