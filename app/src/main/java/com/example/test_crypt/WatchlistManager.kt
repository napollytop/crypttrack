package com.example.test_crypt

import android.content.Context
import android.content.SharedPreferences

class WatchlistManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("watchlist_prefs", Context.MODE_PRIVATE)

    fun addToWatchlist(coinId: String) {
        val watchlist = getWatchlist().toMutableSet()
        watchlist.add(coinId)
        prefs.edit().putStringSet("watchlist_ids", watchlist).apply()
    }

    fun removeFromWatchlist(coinId: String) {
        val watchlist = getWatchlist().toMutableSet()
        watchlist.remove(coinId)
        prefs.edit().putStringSet("watchlist_ids", watchlist).apply()
    }

    fun isInWatchlist(coinId: String): Boolean {
        return getWatchlist().contains(coinId)
    }

    fun getWatchlist(): Set<String> {
        return prefs.getStringSet("watchlist_ids", emptySet()) ?: emptySet()
    }
}
