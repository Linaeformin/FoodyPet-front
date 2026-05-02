package com.example.foodypet.stock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.databinding.ItemStockBinding
import com.example.foodypet.stock.enum.StockScreenMode
import com.example.foodypet.stock.model.StockItem

class StockAdapter(
    private val screenMode: StockScreenMode = StockScreenMode.VIEW,
    private val onItemClick: (StockItem) -> Unit = {},
    private val onCountChanged: (StockItem, String) -> Unit = { _, _ -> },
    private val onCheckClick: (StockItem) -> Unit = {}
) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    private val items = mutableListOf<StockItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<StockItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class StockViewHolder(
        private val binding: ItemStockBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StockItem) = with(binding) {
            stockExpireDateTv.text = "~${item.expireDate}"
            stockNameTv.text = item.name

            // RecyclerView 재사용 때문에 클릭 리스너/상태 초기화 필수
            root.setOnClickListener(null)
            stockContentLayout.setOnClickListener(null)
            stockCheckIv.setOnClickListener(null)
            stockCountEditLayout.setOnClickListener(null)
            stockPlusIv.setOnClickListener(null)
            stockMinusIv.setOnClickListener(null)

            when (screenMode) {
                StockScreenMode.VIEW -> {
                    stockCheckIv.visibility = View.GONE

                    stockCountTv.visibility = View.VISIBLE
                    stockCountEditLayout.visibility = View.GONE

                    stockCountTv.text = item.count

                    stockContentLayout.setOnClickListener {
                        onItemClick(item)
                    }
                }

                StockScreenMode.EDIT -> {
                    stockCheckIv.visibility = View.GONE

                    stockCountTv.visibility = View.GONE
                    stockCountEditLayout.visibility = View.VISIBLE

                    stockEditCountTv.text = item.count

                    stockContentLayout.setOnClickListener {
                        onItemClick(item)
                    }

                    // +, - 영역 클릭했을 때 아이템 클릭으로 넘어가는 것 방지
                    stockCountEditLayout.setOnClickListener {
                        // 일부러 비워둠
                    }

                    stockPlusIv.setOnClickListener {
                        val newCount = increaseCount(item.count)
                        onCountChanged(item, newCount)
                    }

                    stockMinusIv.setOnClickListener {
                        val newCount = decreaseCount(item.count)
                        onCountChanged(item, newCount)
                    }
                }

                StockScreenMode.DELETE -> {
                    stockCheckIv.visibility = View.VISIBLE

                    stockCountTv.visibility = View.VISIBLE
                    stockCountEditLayout.visibility = View.GONE

                    stockCountTv.text = item.count

                    stockCheckIv.setImageResource(
                        if (item.isSelected) {
                            R.drawable.icon_square_check
                        } else {
                            R.drawable.icon_square_uncheck
                        }
                    )

                    stockCheckIv.setOnClickListener {
                        onCheckClick(item)
                    }

                    stockContentLayout.setOnClickListener {
                        onCheckClick(item)
                    }
                }
            }
        }
    }

    private fun increaseCount(count: String): String {
        val number = extractNumber(count)
        val unit = extractUnit(count)

        return "${number + 1}$unit"
    }

    private fun decreaseCount(count: String): String {
        val number = extractNumber(count)
        val unit = extractUnit(count)

        val decreasedNumber = if (number > 0) {
            number - 1
        } else {
            0
        }

        return "$decreasedNumber$unit"
    }

    private fun extractNumber(count: String): Int {
        return count.filter { it.isDigit() }.toIntOrNull() ?: 0
    }

    private fun extractUnit(count: String): String {
        return count.filter { !it.isDigit() }
    }
}