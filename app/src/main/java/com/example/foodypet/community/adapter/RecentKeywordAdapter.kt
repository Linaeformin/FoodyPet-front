package com.example.foodypet.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.community.model.RecentKeyword
import com.example.foodypet.databinding.ItemRecentSearchBinding

class RecentKeywordAdapter(
    private val keywordList: List<RecentKeyword>
) : RecyclerView.Adapter<RecentKeywordAdapter.RecentKeywordViewHolder>() {

    inner class RecentKeywordViewHolder(
        private val binding: ItemRecentSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(keyword: RecentKeyword) {
            binding.recentSearchItemKeywordTv.text = keyword.keyword
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentKeywordViewHolder {
        val binding = ItemRecentSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecentKeywordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentKeywordViewHolder, position: Int) {
        holder.bind(keywordList[position])
    }

    override fun getItemCount(): Int = keywordList.size
}