package com.example.foodypet.community.mapper

import com.example.foodypet.community.dto.CommunityPostListResponse
import com.example.foodypet.community.model.CommunityPost
import com.example.foodypet.community.model.MealInfo
import java.util.Locale

object CommunityPostMapper {

    fun toModel(response: CommunityPostListResponse): CommunityPost {
        return CommunityPost(
            postId = response.postId,
            profileImageUrl = response.profileImageUrl,
            nickname = response.nickname,
            tag = response.tag,
            title = response.title,
            imgUrl = response.imgUrl,
            content = response.context,
            meal = response.meal.map { meal ->
                MealInfo(
                    foodName = meal.foodName,
                    amountText = "${formatAmount(meal.amount)}${convertUnit(meal.unit)}"
                )
            },
            likeCount = response.likeCount,
            commentCount = response.commentCount,
            isMyPost = response.isMine
        )
    }

    private fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            String.format(Locale.KOREA, "%.2f", amount)
        }
    }

    private fun convertUnit(unit: String): String {
        return when (unit.uppercase()) {
            "GRAM" -> "g"
            "G" -> "g"
            "COUNT" -> "개"
            "EA" -> "개"
            "ML" -> "ml"
            "L" -> "L"
            "KG" -> "kg"
            else -> unit
        }
    }
}