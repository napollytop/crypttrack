package com.example.test_crypt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.test_crypt.databinding.FragmentFirstBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WatchlistFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CoinAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        adapter = CoinAdapter(
            coins = emptyList(),
            onFavoriteClick = { coin ->
                removeFromWatchlist(coin)
            },
            onItemClick = { coin ->
                val bundle = Bundle().apply { putString("coinId", coin.id) }
                findNavController().navigate(R.id.DetailFragment, bundle)
            }
        )
        binding.rvCoin.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            fetchWatchlistData()
        }

        fetchWatchlistData()
    }

    private fun fetchWatchlistData() {
        val token = sessionManager.getToken()
        if (token == null) {
            _binding?.swipeRefresh?.isRefreshing = false
            context?.let { Toast.makeText(it, "Silakan login untuk melihat watchlist", Toast.LENGTH_SHORT).show() }
            return
        }
        val bearerToken = "Bearer $token"
        
        if (_binding?.swipeRefresh?.isRefreshing != true) {
            _binding?.progressBar?.visibility = View.VISIBLE
        }
        
        RetrofitClient.backendInstance.getWatchlist(bearerToken).enqueue(object : Callback<WatchlistGetResponse> {
            override fun onResponse(call: Call<WatchlistGetResponse>, response: Response<WatchlistGetResponse>) {
                val binding = _binding ?: return
                if (response.isSuccessful) {
                    val ids = response.body()?.watchlist ?: emptyList()
                    if (ids.isEmpty()) {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        adapter.updateData(emptyList())
                        context?.let { Toast.makeText(it, "Watchlist kosong", Toast.LENGTH_SHORT).show() }
                        return
                    }
                    
                    val idsString = ids.joinToString(",")
                    fetchCoinsFromGecko(idsString)
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Log.e("WatchlistFragment", "Gagal ambil watchlist: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WatchlistGetResponse>, t: Throwable) {
                val binding = _binding ?: return
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Log.e("WatchlistFragment", "Error: ${t.message}")
            }
        })
    }

    private fun fetchCoinsFromGecko(ids: String) {
        RetrofitClient.instance.getCoins(currency = "usd", sparkline = true, ids = ids)
            .enqueue(object : Callback<List<CoinResponse>> {
                override fun onResponse(call: Call<List<CoinResponse>>, response: Response<List<CoinResponse>>) {
                    val binding = _binding ?: return
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    if (response.isSuccessful) {
                        adapter.updateData(response.body() ?: emptyList())
                    } else if (response.code() == 429) {
                        context?.let { Toast.makeText(it, "Terlalu banyak permintaan (Gecko 429).", Toast.LENGTH_SHORT).show() }
                    }
                }

                override fun onFailure(call: Call<List<CoinResponse>>, t: Throwable) {
                    val binding = _binding ?: return
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Log.e("WatchlistFragment", "Gecko Error: ${t.message}")
                }
            })
    }

    private fun removeFromWatchlist(coin: CoinResponse) {
        val token = "Bearer ${sessionManager.getToken()}"
        RetrofitClient.backendInstance.removeFromWatchlist(token, coin.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "${coin.name} dihapus dari watchlist", Toast.LENGTH_SHORT).show()
                    fetchWatchlistData()
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
