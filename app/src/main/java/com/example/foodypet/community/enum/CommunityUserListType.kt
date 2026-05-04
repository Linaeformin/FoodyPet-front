package com.example.foodypet.community.enum

enum class CommunityUserListType(
    val title: String
) {
    BLOCKED("차단한 사용자"),
    FOLLOWER("팔로우"),
    FOLLOWING("팔로잉"),
    NOTIFICATION("알림")
}