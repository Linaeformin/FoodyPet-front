package com.example.foodypet.stock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.databinding.ItemStockAssignBinding
import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.model.StockItem

class StockAssignAdapter(
    private val onClickNutrition: (StockItem) -> Unit,
    private val onClickAssign: (StockItem) -> Unit
) : RecyclerView.Adapter<StockAssignAdapter.StockAssignViewHolder>() {

    private val items = mutableListOf<StockItem>()

    fun submitList(newItems: List<StockItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StockAssignViewHolder {
        val binding = ItemStockAssignBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StockAssignViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockAssignViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class StockAssignViewHolder(
        private val binding: ItemStockAssignBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StockItem) {
            binding.productNameTv.text = item.name
            binding.productCategoryTv.text = getCategoryText(item.category)

            // 현재 StockItem에 이미지 필드가 없어서 임시 이미지 사용
            binding.productIv.setImageResource(R.drawable.image_rampocket)

            binding.nutritionBtn.setOnClickListener {
                onClickNutrition(item)
            }

            binding.stockAssignBtn.setOnClickListener {
                onClickAssign(item)
            }
        }

        private fun getCategoryText(category: StockCategory): String {
            return when (category) {
                StockCategory.COOKED -> "화식"
                StockCategory.WET -> "습식"
                StockCategory.FRESH -> "생식"
                StockCategory.DRY -> "건식"
                StockCategory.SNACK -> "간식"
            }
        }
    }
}