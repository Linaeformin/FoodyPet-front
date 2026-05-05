package com.example.foodypet.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.databinding.ItemMyPetAddBinding
import com.example.foodypet.databinding.ItemMyPetBinding
import com.example.foodypet.mypage.model.MyPet
import com.example.foodypet.mypage.model.PetGender

class MyPetAdapter(
    private val petList: List<MyPet>,
    private val onEditClick: (MyPet) -> Unit,
    private val onDeleteClick: (MyPet) -> Unit,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PET = 0
        private const val VIEW_TYPE_ADD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == petList.size) {
            VIEW_TYPE_ADD
        } else {
            VIEW_TYPE_PET
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PET -> {
                val binding = ItemMyPetBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MyPetViewHolder(binding)
            }

            else -> {
                val binding = ItemMyPetAddBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MyPetAddViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyPetViewHolder -> {
                holder.bind(petList[position])
            }

            is MyPetAddViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return petList.size + 1
    }

    inner class MyPetViewHolder(
        private val binding: ItemMyPetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pet: MyPet) {
            binding.myPetProfileImgIv.setImageResource(pet.imageResId)
            binding.myPetNameTv.text = pet.name
            binding.myPetBirthTv.text = pet.birth
            binding.myPetTypeTv.text = pet.type
            binding.myPetBreedTv.text = pet.breed

            binding.myPetNeuteredTv.text =
                if (pet.isNeutered) "중성화 완료" else "중성화 안 함"

            binding.myPetGenderIv.setImageResource(
                when (pet.gender) {
                    PetGender.GIRL -> R.drawable.icon_girl
                    PetGender.BOY -> R.drawable.icon_boy
                }
            )

            binding.myPetFeedCountTv.text = pet.feedCount
            binding.myPetFeedTimeTv.text = pet.feedTime
            binding.myPetSupplementTv.text = pet.supplement

            binding.myPetEditBtn.setOnClickListener {
                onEditClick(pet)
            }

            binding.myPetDeleteIv.setOnClickListener {
                onDeleteClick(pet)
            }
        }
    }

    inner class MyPetAddViewHolder(
        private val binding: ItemMyPetAddBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.myPetAddCardRoot.setOnClickListener {
                onAddClick()
            }

            binding.myPetAddContentArea.setOnClickListener {
                onAddClick()
            }
        }
    }
}