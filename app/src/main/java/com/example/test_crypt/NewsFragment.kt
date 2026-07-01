package com.example.test_crypt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.test_crypt.databinding.FragmentNewsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NewsAdapter(emptyList())
        binding.rvNews.adapter = adapter

        fetchNews()
    }

    private fun fetchNews() {
        _binding?.progressBar?.visibility = View.VISIBLE
        RetrofitClient.newsInstance.getLatestNews()
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    val binding = _binding ?: return
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val articles = response.body()?.results ?: emptyList()
                        adapter.updateData(articles)
                    } else {
                        context?.let { Toast.makeText(it, "Gagal mengambil berita", Toast.LENGTH_SHORT).show() }
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    val binding = _binding ?: return
                    binding.progressBar.visibility = View.GONE
                    Log.e("NewsFragment", "Error: ${t.message}")
                    context?.let { Toast.makeText(it, "Kesalahan jaringan: Periksa internet Anda", Toast.LENGTH_SHORT).show() }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
