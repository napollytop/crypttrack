package com.example.test_crypt

import android.icu.util.Currency
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @GET("coins/markets")
    fun getCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("sparkline") sparkline: Boolean,
        @Query("ids") ids: String? = null
    ): Call<List<CoinResponse>>

    @GET("coins/{id}")
    fun getCoinDetail(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developer_data: Boolean = false,
        @Query("sparkline") sparkline: Boolean = true
    ): Call<CoinDetailResponse>

}

interface BackendApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<Void>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("profile")
    fun getProfile(@Header("Authorization") token: String): Call<ProfileResponse>

    @GET("watchlist")
    fun getWatchlist(@Header("Authorization") token: String): Call<WatchlistGetResponse>

    @POST("watchlist")
    fun addToWatchlist(@Header("Authorization") token: String, @Body request: WatchlistAddRequest): Call<Void>

    @DELETE("watchlist/{coinID}")
    fun removeFromWatchlist(@Header("Authorization") token: String, @Path("coinID") coinID: String): Call<Void>
}

interface NewsApiService {
    @GET("latest")
    fun getLatestNews(
        @Query("apikey") apiKey: String = "pub_8dd9485314264836a3c6c8de6757b45b",
        @Query("q") query: String = "crypto",
        @Query("language") language: String = "en"
    ): Call<NewsResponse>
}
