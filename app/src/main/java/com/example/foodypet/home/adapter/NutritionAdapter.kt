package com.example.foodypet.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.databinding.ItemCapsuleBinding
import com.example.foodypet.databinding.ItemNutritionRowBinding
import com.example.foodypet.home.model.NutritionUiModel

class NutritionAdapter(
    private val items: MutableList<NutritionUiModel>
) : RecyclerView.Adapter<NutritionAdapter.NutritionViewHolder>() {

    inner class NutritionViewHolder(
        private val binding: ItemNutritionRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NutritionUiModel) {
            binding.nutritionNameTv.text = item.nutritionName

            binding.capsuleContainerLayout.removeAllViews()

            repeat(item.requiredCount) { index ->
                val capsuleBinding = ItemCapsuleBinding.inflate(
                    LayoutInflater.from(binding.root.context),
                    binding.capsuleContainerLayout,
                    false
                )

                setCapsuleImage(
                    capsuleImageView = capsuleBinding.capsuleIv,
                    isSelected = index < item.takenCount
                )

                capsuleBinding.capsuleIv.setOnClickListener {
                    val adapterPosition = bindingAdapterPosition
                    if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                    val currentCount = items[adapterPosition].takenCount
                    val clickedCount = index + 1

                    items[adapterPosition].takenCount =
                        if (currentCount == clickedCount) {
                            clickedCount - 1
                        } else {
                            clickedCount
                        }

                    notifyItemChanged(adapterPosition)
                }

                binding.capsuleContainerLayout.addView(capsuleBinding.root)
            }
        }

        private fun setCapsuleImage(capsuleImageView: ImageView, isSelected: Boolean) {
            if (isSelected) {
                capsuleImageView.setImageResource(R.drawable.icon_capsule)
            } else {
                capsuleImageView.setImageResource(R.drawable.icon_capsule_unselect)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionViewHolder {
        val binding = ItemNutritionRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NutritionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NutritionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun getCurrentItems(): List<NutritionUiModel> = items

    fun submitList(newItems: List<NutritionUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}