package com.example.foodypet.community.model

data class CommunityPost(
    val postId: Long,
    val profileImageUrl: String?,
    val nickname: String,
    val tag: String,
    val title: String,
    val imgUrl: String?,
    val content: String,
    val meal: List<MealInfo>,
    var likeCount: Int,
    var commentCount: Int,
    val isMyPost: Boolean,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false
)