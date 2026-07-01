package com.example.test_crypt

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val results: List<Article>
)

data class Article(
    val title: String,
    val link: String,
    val description: String?,
    val pubDate: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("source_id")
    val sourceId: String?
)
