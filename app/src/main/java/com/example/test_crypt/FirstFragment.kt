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

class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
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

        binding.rvCoin.adapter = CoinAdapter(
            coins = emptyList(),
            onFavoriteClick = { coin ->
                toggleWatchlist(coin)
            },
            onItemClick = { coin ->
                val bundle = Bundle().apply { putString("coinId", coin.id) }
                findNavController().navigate(R.id.DetailFragment, bundle)
            }
        )

        binding.swipeRefresh.setOnRefreshListener {
            fetchCoinData()
        }

        fetchCoinData()
    }

    private fun fetchCoinData() {
        if (_binding?.swipeRefresh?.isRefreshing != true) {
            _binding?.progressBar?.visibility = View.VISIBLE
        }
        
        RetrofitClient.instance.getCoins(currency = "usd", sparkline = true)
            .enqueue(object : Callback<List<CoinResponse>> {
                override fun onResponse(call: Call<List<CoinResponse>>, response: Response<List<CoinResponse>>) {
                    val binding = _binding ?: return
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    if (response.isSuccessful) {
                        val coins = response.body() ?: emptyList()
                        (binding.rvCoin.adapter as? CoinAdapter)?.updateData(coins)
                    } else if (response.code() == 429) {
                        Log.e("FirstFragment", "Rate Limit Exceeded (429)")
                        context?.let { Toast.makeText(it, "Terlalu banyak permintaan. Silakan tunggu sebentar.", Toast.LENGTH_LONG).show() }
                    } else {
                        Log.e("FirstFragment", "Request Gagal: ${response.code()} ${response.message()}")
                        context?.let { Toast.makeText(it, "Gagal memuat data", Toast.LENGTH_SHORT).show() }
                    }
                }
                override fun onFailure(call: Call<List<CoinResponse>>, t: Throwable) {
                    val binding = _binding ?: return
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Log.e("FirstFragment", "Error: ${t.message}", t)
                    context?.let { Toast.makeText(it, "Kesalahan Jaringan: Periksa koneksi Anda", Toast.LENGTH_SHORT).show() }
                }
            })
    }

    private fun toggleWatchlist(coin: CoinResponse) {
        val token = sessionManager.getToken()
        if (token == null) {
            context?.let { Toast.makeText(it, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show() }
            return
        }
        val bearerToken = "Bearer $token"
        
        // Check if already in watchlist
        RetrofitClient.backendInstance.getWatchlist(bearerToken).enqueue(object : Callback<WatchlistGetResponse> {
            override fun onResponse(call: Call<WatchlistGetResponse>, response: Response<WatchlistGetResponse>) {
                if (_binding == null) return
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
                    // Update UI icon bintang
                    binding.rvCoin.adapter?.notifyDataSetChanged()
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
                    // Update UI icon bintang
                    binding.rvCoin.adapter?.notifyDataSetChanged()
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
