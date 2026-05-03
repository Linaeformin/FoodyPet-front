package com.example.foodypet.community.model

sealed class CommunitySearchResult {

    data class Keyword(
        val keyword: String
    ) : CommunitySearchResult()

    data class Profile(
        val profileImageRes: Int,
        val nickname: String,
        val petType: String,
        val description: String
    ) : CommunitySearchResult()
}