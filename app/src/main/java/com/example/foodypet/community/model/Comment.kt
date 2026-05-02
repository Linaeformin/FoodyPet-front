package com.example.foodypet.community.model

data class Comment(
    val id: Int,
    val parentId: Int? = null,
    val nickname: String,
    val content: String,
    val time: String,
    val profileImageRes: Int,
    val isMine: Boolean,
    val isReply: Boolean = false,
    var isLiked: Boolean = false
)