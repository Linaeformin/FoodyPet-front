package com.example.foodypet.community.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.community.adapter.CommentAdapter
import com.example.foodypet.community.model.Comment
import com.example.foodypet.databinding.BottomSheetCommentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommentBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCommentBinding? = null
    private val binding get() = _binding!!

    private lateinit var commentAdapter: CommentAdapter

    private val commentList = mutableListOf<Comment>()

    private var selectedParentComment: Comment? = null
    private var editingComment: Comment? = null

    private var nextCommentId = 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)

            it.layoutParams.height = (resources.displayMetrics.heightPixels * 0.9).toInt()
            it.requestLayout()

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDummyComments()
        initRecyclerView()
        initClickListeners()
    }

    private fun initDummyComments() {
        commentList.clear()

        commentList.add(
            Comment(
                id = 1,
                parentId = null,
                nickname = "내몸은주둥",
                content = "복돼지 치즈볼 어디 간거야?",
                time = "3시간",
                profileImageRes = R.drawable.cat_1,
                isMine = false,
                isReply = false
            )
        )

        commentList.add(
            Comment(
                id = 2,
                parentId = 1,
                nickname = "냠냠이먹자",
                content = "하림에서 새로 나왔어요!",
                time = "3시간",
                profileImageRes = R.drawable.cat_2,
                isMine = true,
                isReply = true
            )
        )
    }

    private fun initRecyclerView() {
        commentAdapter = CommentAdapter(
            comments = commentList,
            onReplyClick = { comment ->
                startReplyMode(comment)
            },
            onEditClick = { comment ->
                startEditMode(comment)
            },
            onDeleteClick = { comment ->
                deleteComment(comment)
            },
            onReportClick = {
                Toast.makeText(requireContext(), "댓글을 신고했어", Toast.LENGTH_SHORT).show()
            },
            onBlockClick = {
                Toast.makeText(requireContext(), "사용자를 차단했어", Toast.LENGTH_SHORT).show()
            }
        )

        binding.commentRv.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initClickListeners() {
        binding.commentSendIv.setOnClickListener {
            submitComment()
        }
    }

    private fun startReplyMode(comment: Comment) {
        selectedParentComment = if (comment.isReply) {
            commentList.find { it.id == comment.parentId }
        } else {
            comment
        }

        editingComment = null

        binding.commentInputEt.setText("")
        binding.commentInputEt.hint = "대댓글을 작성해보세요."
        binding.commentInputEt.requestFocus()

        showKeyboard()
    }

    private fun startEditMode(comment: Comment) {
        editingComment = comment
        selectedParentComment = null

        binding.commentInputEt.setText(comment.content)
        binding.commentInputEt.setSelection(binding.commentInputEt.text.length)

        binding.commentInputEt.hint = if (comment.isReply) {
            "대댓글을 수정해보세요."
        } else {
            "댓글을 수정해보세요."
        }

        binding.commentInputEt.requestFocus()

        showKeyboard()
    }

    private fun submitComment() {
        val content = binding.commentInputEt.text.toString().trim()

        if (content.isEmpty()) {
            Toast.makeText(requireContext(), "내용을 입력해줘", Toast.LENGTH_SHORT).show()
            return
        }

        val editTarget = editingComment

        if (editTarget != null) {
            commentAdapter.updateComment(editTarget, content)
            resetInputMode()
            return
        }

        val parentComment = selectedParentComment

        if (parentComment != null) {
            val reply = Comment(
                id = nextCommentId++,
                parentId = parentComment.id,
                nickname = "냠냠이먹자",
                content = content,
                time = "방금",
                profileImageRes = R.drawable.cat_2,
                isMine = true,
                isReply = true
            )

            commentAdapter.addReply(parentComment, reply)

            val replyIndex = commentList.indexOfFirst { it.id == reply.id }
            binding.commentRv.smoothScrollToPosition(replyIndex)

            resetInputMode()
            return
        }

        val comment = Comment(
            id = nextCommentId++,
            parentId = null,
            nickname = "냠냠이먹자",
            content = content,
            time = "방금",
            profileImageRes = R.drawable.cat_2,
            isMine = true,
            isReply = false
        )

        commentAdapter.addComment(comment)
        binding.commentRv.smoothScrollToPosition(commentList.size - 1)

        resetInputMode()
    }

    private fun deleteComment(comment: Comment) {
        commentAdapter.deleteComment(comment)

        if (!comment.isReply) {
            val replyList = commentList.filter { it.parentId == comment.id }

            replyList.forEach {
                commentAdapter.deleteComment(it)
            }
        }

        Toast.makeText(requireContext(), "삭제했어", Toast.LENGTH_SHORT).show()
    }

    private fun resetInputMode() {
        selectedParentComment = null
        editingComment = null

        binding.commentInputEt.setText("")
        binding.commentInputEt.hint = "댓글을 작성해보세요."

        hideKeyboard()
    }

    private fun showKeyboard() {
        binding.commentInputEt.postDelayed({
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.showSoftInput(
                binding.commentInputEt,
                InputMethodManager.SHOW_IMPLICIT
            )
        }, 150)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(
            binding.commentInputEt.windowToken,
            0
        )

        binding.commentInputEt.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}