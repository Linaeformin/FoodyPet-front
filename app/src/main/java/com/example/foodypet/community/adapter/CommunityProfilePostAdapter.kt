package com.example.foodypet.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.databinding.ItemCommunitySearchPostBinding
import com.example.foodypet.community.model.CommunityProfilePost

class CommunityProfilePostAdapter(
    private val onPostClick: (CommunityProfilePost) -> Unit
) : RecyclerView.Adapter<CommunityProfilePostAdapter.ProfilePostViewHolder>() {

    private val posts = mutableListOf<CommunityProfilePost>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostViewHolder {
        val binding = ItemCommunitySearchPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfilePostViewHolder(binding, onPostClick)
    }

    override fun onBindViewHolder(holder: ProfilePostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    fun submitList(newPosts: List<CommunityProfilePost>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    class ProfilePostViewHolder(
        private val binding: ItemCommunitySearchPostBinding,
        private val onPostClick: (CommunityProfilePost) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: CommunityProfilePost) {
            binding.root.setOnClickListener {
                onPostClick(post)
            }

            // TODO: item_community_search_post.xml 안의 ImageView id에 맞게 수정
            // 예시:
            // binding.communityPostImgIv.setImageResource(post.imageResId)
        }
    }
}