package com.example.foodypet.stock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.databinding.ItemStockBinding
import com.example.foodypet.stock.enum.StockScreenMode
import com.example.foodypet.stock.model.StockItem

class StockAdapter(
    private val screenMode: StockScreenMode = StockScreenMode.VIEW,
    private val onItemClick: (StockItem) -> Unit = {},
    private val onCountChanged: (StockItem, String) -> Unit = { _, _ -> }
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

            // 기본적으로 아이템 전체 클릭은 항상 Fragment로 넘김
            root.setOnClickListener {
                onItemClick(item)
            }

            when (screenMode) {
                StockScreenMode.VIEW -> {
                    stockCountTv.visibility = View.VISIBLE
                    stockCountEditLayout.visibility = View.GONE

                    stockCountTv.text = item.count

                    stockPlusIv.setOnClickListener(null)
                    stockMinusIv.setOnClickListener(null)
                    stockCountEditLayout.setOnClickListener(null)
                }

                StockScreenMode.EDIT -> {
                    stockCountTv.visibility = View.GONE
                    stockCountEditLayout.visibility = View.VISIBLE

                    stockEditCountTv.text = item.count

                    // +, - 영역 클릭했을 때 아이템 전체 클릭으로 넘어가는 것 방지
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