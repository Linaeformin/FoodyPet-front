package com.example.foodypet.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.community.model.CommunitySearchResult
import com.example.foodypet.databinding.ItemCommunitySearchKeywordBinding
import com.example.foodypet.databinding.ItemCommunitySearchProfileBinding

class CommunitySearchResultAdapter(
    private var searchResultList: List<CommunitySearchResult>,
    private val onKeywordClick: (String) -> Unit,
    private val onProfileClick: (CommunitySearchResult.Profile) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_KEYWORD = 0
        private const val VIEW_TYPE_PROFILE = 1
    }

    inner class KeywordViewHolder(
        private val binding: ItemCommunitySearchKeywordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommunitySearchResult.Keyword) {
            binding.communitySearchKeywordTv.text = item.keyword

            binding.root.setOnClickListener {
                onKeywordClick(item.keyword)
            }
        }
    }

    inner class ProfileViewHolder(
        private val binding: ItemCommunitySearchProfileBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommunitySearchResult.Profile) {
            binding.communitySearchProfileIv.setImageResource(item.profileImageRes)
            binding.communitySearchProfileNicknameTv.text = item.nickname
            binding.communitySearchProfilePetTypeTv.text = item.petType
            binding.communitySearchProfileDescriptionTv.text = item.description

            binding.root.setOnClickListener {
                onProfileClick(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (searchResultList[position]) {
            is CommunitySearchResult.Keyword -> VIEW_TYPE_KEYWORD
            is CommunitySearchResult.Profile -> VIEW_TYPE_PROFILE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_KEYWORD -> {
                val binding = ItemCommunitySearchKeywordBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                KeywordViewHolder(binding)
            }

            VIEW_TYPE_PROFILE -> {
                val binding = ItemCommunitySearchProfileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ProfileViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = searchResultList[position]) {
            is CommunitySearchResult.Keyword -> {
                (holder as KeywordViewHolder).bind(item)
            }

            is CommunitySearchResult.Profile -> {
                (holder as ProfileViewHolder).bind(item)
            }
        }
    }

    override fun getItemCount(): Int = searchResultList.size

    fun setSearchResults(newSearchResultList: List<CommunitySearchResult>) {
        searchResultList = newSearchResultList
        notifyDataSetChanged()
    }
}