package com.example.foodypet.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.community.model.RecentProfile
import com.example.foodypet.databinding.ItemRecentProfileBinding

class RecentProfileAdapter(
    private val profileList: List<RecentProfile>,
    private val onProfileClick: (RecentProfile) -> Unit
) : RecyclerView.Adapter<RecentProfileAdapter.RecentProfileViewHolder>() {

    inner class RecentProfileViewHolder(
        private val binding: ItemRecentProfileBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(profile: RecentProfile) {
            binding.recentProfileItemPetIv.setImageResource(profile.profileImageRes)
            binding.recentProfileItemNicknameTv.text = profile.nickname

            binding.root.setOnClickListener {
                onProfileClick(profile)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentProfileViewHolder {
        val binding = ItemRecentProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecentProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentProfileViewHolder, position: Int) {
        holder.bind(profileList[position])
    }

    override fun getItemCount(): Int = profileList.size
}