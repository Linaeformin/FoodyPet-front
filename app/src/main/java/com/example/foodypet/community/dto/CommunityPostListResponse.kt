package com.example.foodypet.community.dto

data class CommunityPostListResponse(
    val postId: Long,
    val profileImageUrl: String?,
    val nickname: String,
    val tag: String,
    val title: String,
    val imgUrl: String?,
    val context: String,
    val meal: List<CommunityPostMealResponse>,
    val likeCount: Int,
    val commentCount: Int,
    val isMine: Boolean
)

data class CommunityPostMealResponse(
    val foodName: String,
    val amount: Double,
    val unit: String
)