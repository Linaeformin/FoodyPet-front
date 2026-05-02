package com.example.foodypet.community.model

import com.example.foodypet.community.`enum`.CommunityCategory

data class CommunityPost(
    val id: Int,
    val profileImageRes: Int,
    val nickname: String,
    val category: CommunityCategory,
    val title: String,
    val content: String,
    val mealImageRes: Int,
    var likeCount: Int,
    val commentCount: Int,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false
)