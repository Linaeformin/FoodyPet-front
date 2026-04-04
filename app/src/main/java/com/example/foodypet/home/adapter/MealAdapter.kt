package com.example.foodypet.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.databinding.ItemMealBinding
import com.example.foodypet.home.model.MealItem

class MealAdapter : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private val itemList = mutableListOf<MealItem>()

    fun submitList(newList: List<MealItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    inner class MealViewHolder(
        private val binding: ItemMealBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MealItem) {
            binding.quickMealTimeTv.visibility = View.VISIBLE
            binding.quickMealTimeTv.text = item.time
            binding.quickMealContentTv.text = item.content
        }
    }
}