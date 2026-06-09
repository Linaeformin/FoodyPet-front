package com.example.foodypet.community.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.community.adapter.CommunityPostAdapter
import com.example.foodypet.community.adapter.MealInfoAdapter
import com.example.foodypet.community.mapper.CommunityPostMapper
import com.example.foodypet.community.model.CommunityPost
import com.example.foodypet.databinding.DialogMealInfoBinding
import com.example.foodypet.databinding.FragmentCommunityBinding
import com.example.foodypet.databinding.ViewCommunityProfileMoreMenuBinding
import com.example.foodypet.network.RetrofitClient
import kotlinx.coroutines.launch

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private lateinit var communityPostAdapter: CommunityPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initClickListener()
        loadCommunityPosts()
    }

    override fun onResume() {
        super.onResume()

        if (::communityPostAdapter.isInitialized) {
            loadCommunityPosts()
        }
    }

    private fun initRecyclerView() {
        communityPostAdapter = CommunityPostAdapter(
            onMenuClick = { post, anchorView ->
                showPostMoreMenu(post, anchorView)
            },
            onProfileClick = { post ->
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        CommunityProfileFragment.newInstance(false)
                    )
                    .addToBackStack(null)
                    .commit()
            },
            onOpenMealClick = { post ->
                showMealInfoDialog(post)
            },
            onMoreClick = { post ->
                showFullContent(post)
            },
            onCommentClick = { post ->
                val commentBottomSheet = CommentBottomSheetFragment()
                commentBottomSheet.show(parentFragmentManager, "CommentBottomSheet")
            }
        )

        binding.communityRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = communityPostAdapter
        }
    }

    private fun initClickListener() {
        binding.communityProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    CommunityProfileFragment.newInstance(true)
                )
                .addToBackStack(null)
                .commit()
        }

        binding.communityAddBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommunityPostWriteFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.searchArea.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommunitySearchFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.stockSearchEt.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommunitySearchFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.stockSearchIv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommunitySearchFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadCommunityPosts() {
        lifecycleScope.launch {
            try {
                Log.d("CommunityFragment", "커뮤니티 게시글 목록 조회 요청")

                val response = RetrofitClient.apiService.getCommunityPosts()

                Log.d(
                    "CommunityFragment",
                    "커뮤니티 게시글 목록 조회 응답 code=${response.code()}, isSuccessful=${response.isSuccessful}"
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        val posts = body.map { responseItem ->
                            CommunityPostMapper.toModel(responseItem)
                        }

                        communityPostAdapter.setPosts(posts)

                        Log.d(
                            "CommunityFragment",
                            "커뮤니티 게시글 목록 개수=${posts.size}"
                        )
                    } else {
                        communityPostAdapter.setPosts(emptyList())

                        Toast.makeText(
                            requireContext(),
                            "게시글 목록 응답이 비어 있습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()

                    Log.e(
                        "CommunityFragment",
                        "커뮤니티 게시글 목록 조회 실패 code=${response.code()}, errorBody=$errorBody"
                    )

                    Toast.makeText(
                        requireContext(),
                        "게시글 목록 조회에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("CommunityFragment", "커뮤니티 게시글 목록 조회 통신 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버와 통신 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showPostMoreMenu(post: CommunityPost, anchorView: View) {
        val menuBinding = ViewCommunityProfileMoreMenuBinding.inflate(layoutInflater)

        val popupWindow = PopupWindow(
            menuBinding.root,
            dpToPx(110),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            elevation = dpToPx(4).toFloat()
            width = dpToPx(110)
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }

        if (post.isMyPost) {
            menuBinding.menuBlockTv.text = "수정하기"
            menuBinding.menuAlarmTv.text = "삭제하기"
            menuBinding.menuAlarmLayout.visibility = View.VISIBLE

            menuBinding.menuBlockLayout.setOnClickListener {
                popupWindow.dismiss()

                // TODO: 게시글 수정 화면으로 이동
            }

            menuBinding.menuAlarmLayout.setOnClickListener {
                popupWindow.dismiss()

                // TODO: 게시글 삭제 API 연결
            }
        } else {
            menuBinding.menuBlockTv.text = "차단하기"
            menuBinding.menuAlarmLayout.visibility = View.GONE

            menuBinding.menuBlockLayout.setOnClickListener {
                popupWindow.dismiss()

                // TODO: 사용자 차단 API 연결
            }
        }

        popupWindow.showAsDropDown(
            anchorView,
            -dpToPx(95),
            dpToPx(6)
        )
    }

    private fun showMealInfoDialog(post: CommunityPost) {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogMealInfoBinding.inflate(layoutInflater)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.6f)

            val width = (resources.displayMetrics.widthPixels * 0.86).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val mealInfoAdapter = MealInfoAdapter(post.meal)

        dialogBinding.rvMealItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealInfoAdapter
        }

        dialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val width = (resources.displayMetrics.widthPixels * 0.86).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun showFullContent(post: CommunityPost) {
        Toast.makeText(
            requireContext(),
            post.content,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.communityRv.adapter = null
        _binding = null
    }
}