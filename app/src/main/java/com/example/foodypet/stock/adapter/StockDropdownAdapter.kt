package com.example.foodypet.stock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.databinding.ItemStockDropdownBinding

class StockDropdownAdapter(
    private val onClickKeyword: (String) -> Unit
) : RecyclerView.Adapter<StockDropdownAdapter.StockDropdownViewHolder>() {

    private val keywords = mutableListOf<String>()

    fun submitList(newKeywords: List<String>) {
        keywords.clear()
        keywords.addAll(newKeywords.take(3))
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StockDropdownViewHolder {
        val binding = ItemStockDropdownBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StockDropdownViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockDropdownViewHolder, position: Int) {
        holder.bind(keywords[position])
    }

    override fun getItemCount(): Int = keywords.size

    inner class StockDropdownViewHolder(
        private val binding: ItemStockDropdownBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(keyword: String) {
            binding.dropdownKeywordTv.text = keyword

            binding.root.setOnClickListener {
                onClickKeyword(keyword)
            }
        }
    }
}