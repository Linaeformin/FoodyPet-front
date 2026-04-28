package com.example.foodypet.stock.model

import com.example.foodypet.stock.enum.StockCategory

data class StockItem(
    val expireDate: String,
    val name: String,
    val count: String,
    val category: StockCategory,
    val isExpired: Boolean
)