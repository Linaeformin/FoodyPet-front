package com.example.foodypet.stock.dto

data class StockResponse(
    val availableStocks: List<StockItemResponse>,
    val expiredStocks: List<StockItemResponse>
)