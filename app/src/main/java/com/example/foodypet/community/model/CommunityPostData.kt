package com.example.foodypet.community.model

import com.example.foodypet.R
import com.example.foodypet.community.`enum`.CommunityCategory

object CommunityPostData {

    fun getCommunityPosts(): MutableList<CommunityPost> {
        return mutableListOf(
            CommunityPost(
                id = 1,
                profileImageRes = R.drawable.cat_1,
                nickname = "냠냠이먹자",
                category = CommunityCategory.CAT,
                title = "고양이도 먹는 삼계탕",
                content = "링이한테 고양이 전용 삼계탕을 먹여봤어요!",
                mealImageRes = R.drawable.img_community_meal,
                likeCount = 123,
                commentCount = 22
            ),
            CommunityPost(
                id = 2,
                profileImageRes = R.drawable.cat_1,
                nickname = "냠냠이먹자",
                category = CommunityCategory.CAT,
                title = "고양이도 먹는 삼계탕",
                content = "링이한테 고양이 전용 삼계탕을 먹여봤어요!",
                mealImageRes = R.drawable.img_community_meal,
                likeCount = 123,
                commentCount = 22
            ),
            CommunityPost(
                id = 3,
                profileImageRes = R.drawable.cat_1,
                nickname = "냠냠이먹자",
                category = CommunityCategory.CAT,
                title = "고양이도 먹는 삼계탕",
                content = "링이한테 고양이 전용 삼계탕을 먹여봤어요!",
                mealImageRes = R.drawable.img_community_meal,
                likeCount = 123,
                commentCount = 22
            )
        )
    }
}