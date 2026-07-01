package com.example.test_crypt

import com.google.gson.annotations.SerializedName

data class CoinDetailResponse(
    val id: String,
    val symbol: String,
    val name: String,
    val description: Description,
    val image: Image,
    @SerializedName("market_data")
    val marketData: MarketData
)

data class Description(
    val en: String
)

data class Image(
    val thumb: String,
    val small: String,
    val large: String
)

data class MarketData(
    @SerializedName("current_price")
    val currentPrice: Map<String, Double>,
    @SerializedName("market_cap")
    val marketCap: Map<String, Double>,
    @SerializedName("total_volume")
    val totalVolume: Map<String, Double>,
    @SerializedName("price_change_percentage_24h")
    val priceChange24h: Double,
    val sparkline_7d: SparklineData?
)
