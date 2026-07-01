package com.example.test_crypt

import com.google.gson.annotations.SerializedName

data class CoinResponse(
    val id: String,
    val symbol: String,
    val name: String,

    @SerializedName("current_price")
    val currentPrice: Double,

    @SerializedName("image")
    val imageUrl: String,

    @SerializedName("sparkline_in_7d")
    val sparkline: SparklineData?
)

data class SparklineData(
    val price: List<Double>
)
