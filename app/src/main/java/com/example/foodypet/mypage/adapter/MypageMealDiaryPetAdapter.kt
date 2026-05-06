package com.example.foodypet.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.databinding.ItemMypageMealDiaryPetBinding

class MypageMealDiaryPetAdapter(
    private val onPetClick: (MypageMealDiaryPetItem) -> Unit
) : RecyclerView.Adapter<MypageMealDiaryPetAdapter.MypageMealDiaryPetViewHolder>() {

    private val petList = mutableListOf<MypageMealDiaryPetItem>()
    private var selectedPetId: Long? = null

    inner class MypageMealDiaryPetViewHolder(
        private val binding: ItemMypageMealDiaryPetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MypageMealDiaryPetItem) = with(binding) {
            val isSelected = item.petId == selectedPetId

            mypageMealDiaryPetNameTv.text = item.petName

            mypageMealDiaryPetNameTv.setBackgroundResource(
                if (isSelected) R.drawable.bg_orange_fill_8
                else R.drawable.bg_orange_stroke_8
            )

            mypageMealDiaryPetNameTv.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    if (isSelected) R.color.white else R.color.black
                )
            )

            root.setOnClickListener {
                val currentIndex = adapterPosition
                if (currentIndex == RecyclerView.NO_POSITION) return@setOnClickListener

                val previousSelectedPetId = selectedPetId
                selectedPetId = item.petId

                val previousIndex = petList.indexOfFirst {
                    it.petId == previousSelectedPetId
                }

                if (previousIndex != -1) notifyItemChanged(previousIndex)
                notifyItemChanged(currentIndex)

                onPetClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MypageMealDiaryPetViewHolder {
        val binding = ItemMypageMealDiaryPetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MypageMealDiaryPetViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MypageMealDiaryPetViewHolder,
        position: Int
    ) {
        holder.bind(petList[position])
    }

    override fun getItemCount(): Int = petList.size

    fun submitList(newList: List<MypageMealDiaryPetItem>) {
        petList.clear()
        petList.addAll(newList)
        notifyDataSetChanged()
    }

    fun setSelectedPetId(petId: Long) {
        selectedPetId = petId
        notifyDataSetChanged()
    }
}

data class MypageMealDiaryPetItem(
    val petId: Long,
    val petName: String
)