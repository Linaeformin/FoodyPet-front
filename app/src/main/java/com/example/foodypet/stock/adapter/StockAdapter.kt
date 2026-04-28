package com.example.foodypet.stock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.databinding.ItemStockBinding
import com.example.foodypet.stock.model.StockItem

class StockAdapter(
    private val onItemClick: (StockItem) -> Unit = {}
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
            stockCountTv.text = item.count

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}