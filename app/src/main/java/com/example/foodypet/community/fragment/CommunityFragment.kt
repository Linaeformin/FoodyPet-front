package com.example.foodypet.community.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.community.adapter.CommunityPostAdapter
import com.example.foodypet.community.`enum`.CommunityCategory
import com.example.foodypet.community.model.CommunityPost
import com.example.foodypet.community.model.CommunityPostData
import com.example.foodypet.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private lateinit var communityPostAdapter: CommunityPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initClickListener()

        // TODO: 나중에 서버/API/DB에서 게시글 데이터 받아온 뒤 setCommunityPosts(posts) 호출
        // 지금은 화면 확인용으로만 임시 호출 가능
        setCommunityPosts(CommunityPostData.getCommunityPosts())
    }

    private fun initRecyclerView() {
        communityPostAdapter = CommunityPostAdapter(
            onMenuClick = {
                // TODO: 메뉴 버튼 클릭 시 처리
            },
            onOpenMealClick = {
                // TODO: 식단 열기 클릭 시 처리
            },
            onMoreClick = {
                // TODO: 더보기 클릭 시 처리
            },
            onCommentClick = {
                // TODO: 댓글 클릭 시 처리
            }
        )

        binding.communityRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = communityPostAdapter
        }
    }

    private fun initClickListener() {
        binding.communityAddBtn.setOnClickListener {
            // TODO: 게시글 작성 화면으로 이동
        }
    }

    private fun setCommunityPosts(posts: List<CommunityPost>) {
        communityPostAdapter.setPosts(posts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.communityRv.adapter = null
        _binding = null
    }
}