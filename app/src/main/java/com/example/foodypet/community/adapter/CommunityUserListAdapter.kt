package com.example.foodypet.community.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.community.enum.CommunityUserListType
import com.example.foodypet.community.model.CommunityUserListItem
import com.example.foodypet.databinding.ItemCommunitySearchProfileBinding

class CommunityUserListAdapter(
    private val listType: CommunityUserListType,
    private val onFollowClick: (CommunityUserListItem) -> Unit,
    private val onMenuClick: (View, CommunityUserListItem) -> Unit,
    private val onItemClick: (CommunityUserListItem) -> Unit
) : RecyclerView.Adapter<CommunityUserListAdapter.CommunityUserViewHolder>() {

    private val items = mutableListOf<CommunityUserListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityUserViewHolder {
        val binding = ItemCommunitySearchProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommunityUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityUserViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<CommunityUserListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class CommunityUserViewHolder(
        private val binding: ItemCommunitySearchProfileBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommunityUserListItem) {
            binding.communitySearchProfileIv.setImageResource(item.profileImageResId)
            binding.communitySearchProfileNicknameTv.text = item.nickname
            binding.communitySearchProfilePetTypeTv.text = item.petType
            binding.communitySearchProfileDescriptionTv.text = item.description

            setFollowButton(item)
            setMenuButton(item)
            setNotificationBackground(item)

            binding.root.setOnClickListener {
                onItemClick(item)
            }

            binding.communitySearchFollowBtn.setOnClickListener {
                onFollowClick(item)
            }

            binding.communitySearchMenuBtn.setOnClickListener {
                onMenuClick(binding.communitySearchMenuBtn, item)
            }
        }

        private fun setFollowButton(item: CommunityUserListItem) {
            when (listType) {
                CommunityUserListType.FOLLOWER,
                CommunityUserListType.FOLLOWING -> {
                    binding.communitySearchFollowBtn.visibility = android.view.View.VISIBLE

                    if (item.isFollowing) {
                        binding.communitySearchFollowBtn.text = "팔로우 중"
                        binding.communitySearchFollowBtn.setBackgroundResource(R.drawable.bg_orange_stroke_16)
                    } else {
                        binding.communitySearchFollowBtn.text = "팔로우"
                        binding.communitySearchFollowBtn.setBackgroundResource(R.drawable.bg_orange_fill_16)
                    }
                }

                CommunityUserListType.BLOCKED,
                CommunityUserListType.NOTIFICATION -> {
                    binding.communitySearchFollowBtn.visibility = android.view.View.GONE
                }
            }
        }

        private fun setMenuButton(item: CommunityUserListItem) {
            when (listType) {
                CommunityUserListType.BLOCKED,
                CommunityUserListType.FOLLOWER,
                CommunityUserListType.FOLLOWING -> {
                    binding.communitySearchMenuBtn.visibility = android.view.View.VISIBLE
                }

                CommunityUserListType.NOTIFICATION -> {
                    binding.communitySearchMenuBtn.visibility = android.view.View.GONE
                }
            }
        }

        private fun setNotificationBackground(item: CommunityUserListItem) {
            val backgroundColor = when {
                listType == CommunityUserListType.NOTIFICATION && item.isUnreadNotification -> {
                    ContextCompat.getColor(binding.root.context, R.color.white_orange)
                }

                else -> {
                    ContextCompat.getColor(binding.root.context, R.color.white)
                }
            }

            binding.root.setBackgroundColor(backgroundColor)
        }
    }
}