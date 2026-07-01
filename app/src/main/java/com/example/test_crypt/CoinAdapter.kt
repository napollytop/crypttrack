package com.example.test_crypt

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test_crypt.databinding.ItemCoinBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class CoinAdapter(
    private var coins: List<CoinResponse>,
    private val onFavoriteClick: (CoinResponse) -> Unit = {},
    private val onItemClick: (CoinResponse) -> Unit = {}
) : RecyclerView.Adapter<CoinAdapter.CoinViewHolder>() {

    class CoinViewHolder(val binding: ItemCoinBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateData(newCoins: List<CoinResponse>) {
        coins = newCoins
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val binding = ItemCoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        val coin = coins[position]
        holder.binding.apply {
            coinName.text = coin.name
            coinSymbol.text = coin.symbol
            coinPrice.text = "$ ${String.format("%,.2f", coin.currentPrice)}"

            val watchlistManager = WatchlistManager(root.context)
            if (watchlistManager.isInWatchlist(coin.id)) {
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                btnFavorite.setColorFilter(Color.parseColor("#00D09E")) // Teal for active
            } else {
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_off)
                btnFavorite.setColorFilter(Color.parseColor("#848E9C")) // Gray for inactive
            }

            btnFavorite.setOnClickListener {
                onFavoriteClick(coin)
            }

            root.setOnClickListener {
                onItemClick(coin)
            }

            Glide.with(root.context)
                .load(coin.imageUrl)
                .into(coinIcon)

            setupChart(holder, coin.sparkline?.price ?: emptyList())
        }
    }

    override fun getItemCount(): Int = coins.size

    private fun setupChart(holder: CoinViewHolder, prices: List<Double>) {
        if (prices.isEmpty()) {
            holder.binding.coinChart.clear()
            return
        }

        val entries = prices.mapIndexed { index, price ->
            Entry(index.toFloat(), price.toFloat())
        }

        val chartColor = if (prices.last() >= prices.first()) Color.parseColor("#00D09E") else Color.parseColor("#F6465D")

        val dataSet = LineDataSet(entries, "").apply {
            color = chartColor
            setDrawCircles(false)
            setDrawValues(false)
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = chartColor
            fillAlpha = 20
        }

        holder.binding.coinChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }
    }
}
