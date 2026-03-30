package com.example.foodypet.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.databinding.ItemMealPageBinding
import com.example.foodypet.home.model.MealPageUiModel

class MealPagerAdapter(
    private val pages: List<MealPageUiModel>,
    private val inventoryItems: List<String>,
    private val isTimeMode: Boolean
) : RecyclerView.Adapter<MealPagerAdapter.MealPageViewHolder>() {

    inner class MealPageViewHolder(private val binding: ItemMealPageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(page: MealPageUiModel) {
            binding.pageTitleChipTv.text = page.label

            val adapter = FoodRowAdapter(
                items = page.foods.toMutableList(),
                inventoryItems = inventoryItems
            )

            binding.foodRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.foodRecyclerView.adapter = adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMealPageBinding.inflate(inflater, parent, false)
        return MealPageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealPageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size
}