package com.example.foodypet.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.community.model.CommunitySearchProfile
import com.example.foodypet.databinding.ItemCommunitySearchProfileBinding

class CommunitySearchProfileAdapter :
    RecyclerView.Adapter<CommunitySearchProfileAdapter.CommunitySearchProfileViewHolder>() {

    private val profileList = mutableListOf<CommunitySearchProfile>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommunitySearchProfileViewHolder {
        val binding = ItemCommunitySearchProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommunitySearchProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunitySearchProfileViewHolder, position: Int) {
        holder.bind(profileList[position])
    }

    override fun getItemCount(): Int = profileList.size

    fun submitList(list: List<CommunitySearchProfile>) {
        profileList.clear()
        profileList.addAll(list)
        notifyDataSetChanged()
    }

    class CommunitySearchProfileViewHolder(
        private val binding: ItemCommunitySearchProfileBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommunitySearchProfile) {
            binding.communitySearchProfileIv.setImageResource(item.profileImage)
            binding.communitySearchProfileNicknameTv.text = item.nickname
            binding.communitySearchProfilePetTypeTv.text = item.category
            binding.communitySearchProfileDescriptionTv.text = item.description
        }
    }
}