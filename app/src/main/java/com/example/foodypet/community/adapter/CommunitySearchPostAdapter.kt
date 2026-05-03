package com.example.foodypet.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.community.model.CommunitySearchPost
import com.example.foodypet.databinding.ItemCommunitySearchPostBinding

class CommunitySearchPostAdapter :
    RecyclerView.Adapter<CommunitySearchPostAdapter.CommunitySearchPostViewHolder>() {

    private val postList = mutableListOf<CommunitySearchPost>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommunitySearchPostViewHolder {
        val binding = ItemCommunitySearchPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommunitySearchPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunitySearchPostViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int = postList.size

    fun submitList(list: List<CommunitySearchPost>) {
        postList.clear()
        postList.addAll(list)
        notifyDataSetChanged()
    }

    class CommunitySearchPostViewHolder(
        private val binding: ItemCommunitySearchPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommunitySearchPost) {
            binding.communitySearchPostImgIv.setImageResource(item.postImage)
        }
    }
}