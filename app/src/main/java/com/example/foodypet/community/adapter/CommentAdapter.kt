package com.example.foodypet.community.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.community.model.Comment
import com.example.foodypet.databinding.ItemCommentBinding

class CommentAdapter(
    private val comments: MutableList<Comment>,
    private val onReplyClick: (Comment) -> Unit,
    private val onEditClick: (Comment) -> Unit,
    private val onDeleteClick: (Comment) -> Unit,
    private val onReportClick: (Comment) -> Unit,
    private val onBlockClick: (Comment) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(
        private val binding: ItemCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) {
            binding.commentProfileIv.setImageResource(comment.profileImageRes)
            binding.commentNicknameTv.text = comment.nickname
            binding.commentTimeTv.text = comment.time
            binding.commentContentTv.text = comment.content

            setReplyMargin(comment)
            setLikeIcon(comment)

            binding.commentHeartIv.setOnClickListener {
                comment.isLiked = !comment.isLiked
                setLikeIcon(comment)
            }

            binding.commentReplyIv.setOnClickListener {
                onReplyClick(comment)
            }

            binding.commentMenuIv.setOnClickListener {
                showCommentMenu(it, comment)
            }
        }

        private fun setReplyMargin(comment: Comment) {
            val params = binding.commentContentLayout.layoutParams as ViewGroup.MarginLayoutParams

            params.marginStart = if (comment.isReply) {
                dpToPx(48)
            } else {
                0
            }

            binding.commentContentLayout.layoutParams = params
        }

        private fun setLikeIcon(comment: Comment) {
            if (comment.isLiked) {
                binding.commentHeartIv.setImageResource(R.drawable.icon_heart_fill)
            } else {
                binding.commentHeartIv.setImageResource(R.drawable.icon_heart)
            }
        }

        private fun showCommentMenu(anchor: View, comment: Comment) {
            val context = anchor.context

            val popupView = LayoutInflater.from(context)
                .inflate(R.layout.view_comment_more_menu, null)

            val popupWindow = PopupWindow(
                popupView,
                dpToPx(118),
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            ).apply {
                isOutsideTouchable = true
                setBackgroundDrawable(ColorDrawable())
                elevation = dpToPx(6).toFloat()
            }

            val firstLayout = popupView.findViewById<View>(R.id.menu_first_layout)
            val secondLayout = popupView.findViewById<View>(R.id.menu_second_layout)

            val firstText = popupView.findViewById<TextView>(R.id.menu_first_tv)
            val secondText = popupView.findViewById<TextView>(R.id.menu_second_tv)

            if (comment.isMine) {
                firstText.text = if (comment.isReply) {
                    "대댓글 수정하기"
                } else {
                    "댓글 수정하기"
                }

                secondText.text = if (comment.isReply) {
                    "대댓글 삭제하기"
                } else {
                    "댓글 삭제하기"
                }

                firstLayout.setOnClickListener {
                    popupWindow.dismiss()
                    onEditClick(comment)
                }

                secondLayout.setOnClickListener {
                    popupWindow.dismiss()
                    onDeleteClick(comment)
                }
            } else {
                firstText.text = if (comment.isReply) {
                    "대댓글 신고하기"
                } else {
                    "댓글 신고하기"
                }

                secondText.text = "사용자 차단하기"

                firstLayout.setOnClickListener {
                    popupWindow.dismiss()
                    onReportClick(comment)
                }

                secondLayout.setOnClickListener {
                    popupWindow.dismiss()
                    onBlockClick(comment)
                }
            }

            popupWindow.showAsDropDown(anchor, -dpToPx(108), dpToPx(4))
        }

        private fun dpToPx(dp: Int): Int {
            return (dp * binding.root.context.resources.displayMetrics.density).toInt()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun addComment(comment: Comment) {
        comments.add(comment)
        notifyItemInserted(comments.size - 1)
    }

    fun addReply(parentComment: Comment, reply: Comment) {
        val parentIndex = comments.indexOfFirst { it.id == parentComment.id }

        if (parentIndex == -1) {
            comments.add(reply)
            notifyItemInserted(comments.size - 1)
            return
        }

        var insertIndex = parentIndex + 1

        while (
            insertIndex < comments.size &&
            comments[insertIndex].parentId == parentComment.id
        ) {
            insertIndex++
        }

        comments.add(insertIndex, reply)
        notifyItemInserted(insertIndex)
    }

    fun deleteComment(comment: Comment) {
        val index = comments.indexOfFirst { it.id == comment.id }

        if (index != -1) {
            comments.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateComment(comment: Comment, newContent: String) {
        val index = comments.indexOfFirst { it.id == comment.id }

        if (index != -1) {
            comments[index] = comment.copy(content = newContent)
            notifyItemChanged(index)
        }
    }
}