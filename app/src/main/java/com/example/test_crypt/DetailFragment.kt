package com.example.test_crypt

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.test_crypt.databinding.FragmentDetailBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coinId = arguments?.getString("coinId") ?: return
        fetchDetail(coinId)
    }

    private fun fetchDetail(id: String) {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getCoinDetail(id)
            .enqueue(object : Callback<CoinDetailResponse> {
                override fun onResponse(call: Call<CoinDetailResponse>, response: Response<CoinDetailResponse>) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        response.body()?.let { updateUI(it) }
                    } else {
                        Toast.makeText(context, "Gagal mengambil detail koin", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CoinDetailResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Log.e("DetailFragment", "Error: ${t.message}")
                }
            })
    }

    private fun updateUI(detail: CoinDetailResponse) {
        binding.apply {
            detailName.text = detail.name
            detailSymbol.text = detail.symbol
            detailPrice.text = "$ ${detail.marketData.currentPrice["usd"]}"
            
            val priceChange = detail.marketData.priceChange24h
            detailPriceChange.text = String.format("%.2f%%", priceChange)
            detailPriceChange.setTextColor(if (priceChange >= 0) Color.GREEN else Color.RED)

            detailMarketCap.text = "$ ${detail.marketData.marketCap["usd"]}"
            detailVolume.text = "$ ${detail.marketData.totalVolume["usd"]}"

            detailDescription.text = Html.fromHtml(detail.description.en, Html.FROM_HTML_MODE_COMPACT)

            Glide.with(requireContext())
                .load(detail.image.large)
                .into(detailIcon)

            setupChart(detail.marketData.sparkline_7d?.price ?: emptyList())
        }
    }

    private fun setupChart(prices: List<Double>) {
        val entries = prices.mapIndexed { index, price ->
            Entry(index.toFloat(), price.toFloat())
        }

        val dataSet = LineDataSet(entries, "Price (7d)").apply {
            color = Color.BLUE
            setDrawCircles(false)
            setDrawValues(false)
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.BLUE
            fillAlpha = 50
        }

        binding.detailChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            xAxis.isEnabled = false
            animateX(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
