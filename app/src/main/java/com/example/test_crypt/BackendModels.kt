package com.example.test_crypt

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class WatchlistAddRequest(
    val coin_id: String
)

data class WatchlistGetResponse(
    val watchlist: List<String>
)

data class ProfileResponse(
    val id: Int,
    val name: String,
    val email: String
)
