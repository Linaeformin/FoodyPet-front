package com.example.foodypet.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.databinding.ItemMealDiaryBinding
import com.example.foodypet.home.model.MealDiaryItem

class MealDiaryAdapter(
    private val itemList: List<MealDiaryItem>,
    private val onItemClick: (MealDiaryItem) -> Unit
) : RecyclerView.Adapter<MealDiaryAdapter.MealDiaryViewHolder>() {

    inner class MealDiaryViewHolder(
        private val binding: ItemMealDiaryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MealDiaryItem) = with(binding) {
            tvTime.text = item.time
            tvStatus.text = item.status
            tvFoodDesc.text = item.foodDesc
            tvMemo.text = item.memo
            ivFood.setImageResource(item.imageResId)

            setPreferenceIcons(item.preferenceCount)

            root.setOnClickListener {
                onItemClick(item)
            }
        }

        private fun setPreferenceIcons(preferenceCount: Int) = with(binding) {
            val iconList = listOf(
                ivPreference1,
                ivPreference2,
                ivPreference3,
                ivPreference4,
                ivPreference5
            )

            iconList.forEachIndexed { index, imageView ->
                if (index < preferenceCount) {
                    imageView.setImageResource(R.drawable.icon_like)
                } else {
                    imageView.setImageResource(R.drawable.icon_unlike)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealDiaryViewHolder {
        val binding = ItemMealDiaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealDiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealDiaryViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}