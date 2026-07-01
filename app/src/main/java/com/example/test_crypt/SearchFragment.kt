package com.example.test_crypt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.test_crypt.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CoinAdapter
    private lateinit var sessionManager: SessionManager
    private var allCoins: List<CoinResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        adapter = CoinAdapter(
            coins = emptyList(),
            onFavoriteClick = { coin ->
                toggleWatchlist(coin)
            },
            onItemClick = { coin ->
                val bundle = Bundle().apply { putString("coinId", coin.id) }
                findNavController().navigate(R.id.DetailFragment, bundle)
            }
        )
        binding.rvCoin.adapter = adapter

        fetchCoinData()

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterCoins(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCoins(newText)
                return true
            }
        })
    }

    private fun fetchCoinData() {
        _binding?.progressBar?.visibility = View.VISIBLE
        RetrofitClient.instance.getCoins(currency = "usd", sparkline = true)
            .enqueue(object : Callback<List<CoinResponse>> {
                override fun onResponse(call: Call<List<CoinResponse>>, response: Response<List<CoinResponse>>) {
                    val binding = _binding ?: return
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        allCoins = response.body() ?: emptyList()
                        adapter.updateData(allCoins)
                    } else if (response.code() == 429) {
                        context?.let { Toast.makeText(it, "Rate limit tercapai. Silakan coba lagi nanti.", Toast.LENGTH_LONG).show() }
                    }
                }

                override fun onFailure(call: Call<List<CoinResponse>>, t: Throwable) {
                    val binding = _binding ?: return
                    binding.progressBar.visibility = View.GONE
                    Log.e("SearchFragment", "Error: ${t.message}")
                }
            })
    }

    private fun filterCoins(query: String?) {
        val filteredList = if (query.isNullOrBlank()) {
            allCoins
        } else {
            allCoins.filter {
                it.name.contains(query, ignoreCase = true) || it.symbol.contains(query, ignoreCase = true)
            }
        }
        adapter.updateData(filteredList)
    }

    private fun toggleWatchlist(coin: CoinResponse) {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(context, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        val bearerToken = "Bearer $token"
        
        RetrofitClient.backendInstance.getWatchlist(bearerToken).enqueue(object : Callback<WatchlistGetResponse> {
            override fun onResponse(call: Call<WatchlistGetResponse>, response: Response<WatchlistGetResponse>) {
                if (response.isSuccessful) {
                    val watchlist = response.body()?.watchlist ?: emptyList()
                    if (watchlist.contains(coin.id)) {
                        removeFromWatchlist(bearerToken, coin)
                    } else {
                        addToWatchlist(bearerToken, coin)
                    }
                }
            }
            override fun onFailure(call: Call<WatchlistGetResponse>, t: Throwable) {}
        })
    }

    private fun addToWatchlist(token: String, coin: CoinResponse) {
        val req = WatchlistAddRequest(coin.id)
        RetrofitClient.backendInstance.addToWatchlist(token, req).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "${coin.name} ditambahkan ke watchlist", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    private fun removeFromWatchlist(token: String, coin: CoinResponse) {
        RetrofitClient.backendInstance.removeFromWatchlist(token, coin.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "${coin.name} dihapus dari watchlist", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
