package com.example.foodypet.home.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodypet.databinding.ItemHomePetCardBinding
import com.example.foodypet.home.model.PetPagerItem

class HomePetPagerAdapter(
    private val itemList: List<PetPagerItem>
) : RecyclerView.Adapter<HomePetPagerAdapter.PetViewHolder>() {

    inner class PetViewHolder(
        private val binding: ItemHomePetCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PetPagerItem) {
            binding.itemPetNameTv.text = "이름 : ${item.name}"

            Log.d("HomePetPagerAdapter", "pet name = ${item.name}")
            Log.d("HomePetPagerAdapter", "pet imgUrl = ${item.imgUrl}")

            binding.itemPetIv.setImageDrawable(null)

            Glide.with(binding.itemPetIv.context)
                .load(item.imgUrl)
                .placeholder(ColorDrawable(Color.TRANSPARENT))
                .error(ColorDrawable(Color.TRANSPARENT))
                .circleCrop()
                .into(binding.itemPetIv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val binding = ItemHomePetCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}