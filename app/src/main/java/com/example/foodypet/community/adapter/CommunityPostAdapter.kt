package com.example.foodypet.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.community.model.CommunityPost
import com.example.foodypet.databinding.ItemCommunityPostBinding

class CommunityPostAdapter(
    private val onMenuClick: (CommunityPost) -> Unit,
    private val onOpenMealClick: (CommunityPost) -> Unit,
    private val onMoreClick: (CommunityPost) -> Unit,
    private val onCommentClick: (CommunityPost) -> Unit
) : RecyclerView.Adapter<CommunityPostAdapter.CommunityPostViewHolder>() {

    private val postList = mutableListOf<CommunityPost>()

    inner class CommunityPostViewHolder(
        private val binding: ItemCommunityPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: CommunityPost) {
            binding.communityItemProfileIv.setImageResource(post.profileImageRes)
            binding.communityItemNicknameTv.text = post.nickname
            binding.communityItemCategoryTv.text = post.category.displayName
            binding.communityItemTitleTv.text = post.title
            binding.communityItemContentTv.text = post.content
            binding.communityItemMealIv.setImageResource(post.mealImageRes)
            binding.communityItemHeartCountTv.text = post.likeCount.toString()
            binding.communityItemCommentCountTv.text = post.commentCount.toString()

            updateHeartIcon(post)
            updateBookmarkIcon(post)

            binding.communityItemMenuBtn.setOnClickListener {
                onMenuClick(post)
            }

            binding.communityItemOpenMealBtn.setOnClickListener {
                onOpenMealClick(post)
            }

            binding.communityItemMoreTv.setOnClickListener {
                onMoreClick(post)
            }

            binding.communityItemHeartIv.setOnClickListener {
                post.isLiked = !post.isLiked

                if (post.isLiked) {
                    post.likeCount += 1
                } else {
                    post.likeCount -= 1
                }

                binding.communityItemHeartCountTv.text = post.likeCount.toString()
                updateHeartIcon(post)
            }

            binding.communityItemCommentIv.setOnClickListener {
                onCommentClick(post)
            }

            binding.communityItemBookmarkIv.setOnClickListener {
                post.isBookmarked = !post.isBookmarked
                updateBookmarkIcon(post)
            }
        }

        private fun updateHeartIcon(post: CommunityPost) {
            if (post.isLiked) {
                binding.communityItemHeartIv.setImageResource(R.drawable.icon_heart_fill)
            } else {
                binding.communityItemHeartIv.setImageResource(R.drawable.icon_heart)
            }
        }

        private fun updateBookmarkIcon(post: CommunityPost) {
            if (post.isBookmarked) {
                binding.communityItemBookmarkIv.setImageResource(R.drawable.icon_bookmark_fill)
            } else {
                binding.communityItemBookmarkIv.setImageResource(R.drawable.icon_bookmark_unfill)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityPostViewHolder {
        val binding = ItemCommunityPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CommunityPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityPostViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun setPosts(posts: List<CommunityPost>) {
        postList.clear()
        postList.addAll(posts)
        notifyDataSetChanged()
    }
}