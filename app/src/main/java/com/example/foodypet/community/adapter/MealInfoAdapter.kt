package com.example.foodypet.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.community.model.MealInfo
import com.example.foodypet.databinding.ItemMealInfoBinding

class MealInfoAdapter(
    private val mealList: List<MealInfo>
) : RecyclerView.Adapter<MealInfoAdapter.MealInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealInfoViewHolder {
        val binding = ItemMealInfoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealInfoViewHolder, position: Int) {
        holder.bind(mealList[position])
    }

    override fun getItemCount(): Int = mealList.size

    class MealInfoViewHolder(
        private val binding: ItemMealInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MealInfo) {
            binding.tvFoodName.text = item.foodName
            binding.tvFoodAmount.text = item.amount
        }
    }
}