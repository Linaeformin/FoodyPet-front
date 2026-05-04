package com.example.foodypet.community.model

data class CommunityUserListItem(
    val userId: Long,
    val profileImageResId: Int,
    val nickname: String,
    val petType: String,
    val description: String,
    val isFollowing: Boolean = false,
    val isUnreadNotification: Boolean = false
)