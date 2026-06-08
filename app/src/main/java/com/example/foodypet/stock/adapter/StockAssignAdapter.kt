package com.example.foodypet.stock.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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

        fun bind(item: StockItem) = with(binding) {
            productNameTv.text = item.name
            productCategoryTv.text = getCategoryText(item.category)

            Log.d("StockAssignAdapter", "상품 이미지 로드 시도: ${item.name}, ${item.imageUrl}")

            Glide.with(root.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.image_rampocket)
                .error(R.drawable.image_rampocket)
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(
                            "StockAssignAdapter",
                            "상품 이미지 로드 실패: ${item.name}, url=${item.imageUrl}",
                            e
                        )
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d(
                            "StockAssignAdapter",
                            "상품 이미지 로드 성공: ${item.name}, url=${item.imageUrl}"
                        )
                        return false
                    }
                })
                .into(productIv)

            nutritionBtn.setOnClickListener {
                onClickNutrition(item)
            }

            stockAssignBtn.setOnClickListener {
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