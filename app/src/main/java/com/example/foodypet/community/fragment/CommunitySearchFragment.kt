package com.example.foodypet.community.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.community.adapter.RecentKeywordAdapter
import com.example.foodypet.community.adapter.RecentProfileAdapter
import com.example.foodypet.community.model.RecentKeyword
import com.example.foodypet.community.model.RecentProfile
import com.example.foodypet.databinding.FragmentCommunitySearchBinding

class CommunitySearchFragment : Fragment() {

    private var _binding: FragmentCommunitySearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var recentProfileAdapter: RecentProfileAdapter
    private lateinit var recentKeywordAdapter: RecentKeywordAdapter

    private val dummyProfileList = listOf(
        RecentProfile(
            id = 1,
            nickname = "초코",
            profileImageRes = R.drawable.cat_1
        ),
        RecentProfile(
            id = 2,
            nickname = "몽이",
            profileImageRes = R.drawable.cat_1
        ),
        RecentProfile(
            id = 3,
            nickname = "나비",
            profileImageRes = R.drawable.cat_2
        ),
        RecentProfile(
            id = 4,
            nickname = "콩이",
            profileImageRes = R.drawable.cat_2
        ),
        RecentProfile(
            id = 5,
            nickname = "보리",
            profileImageRes = R.drawable.cat_2
        )
    )

    private val dummyKeywordList = listOf(
        RecentKeyword(
            id = 1,
            keyword = "강아지 산책"
        ),
        RecentKeyword(
            id = 2,
            keyword = "고양이 간식"
        ),
        RecentKeyword(
            id = 3,
            keyword = "동물병원"
        ),
        RecentKeyword(
            id = 4,
            keyword = "사료 추천"
        ),
        RecentKeyword(
            id = 5,
            keyword = "펫푸드"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initClickListener()
    }

    private fun initRecyclerView() {
        recentProfileAdapter = RecentProfileAdapter(dummyProfileList)
        binding.communitySearchRecentProfileRv.apply {
            adapter = recentProfileAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }

        recentKeywordAdapter = RecentKeywordAdapter(dummyKeywordList)
        binding.communitySearchRecentKeywordRv.apply {
            adapter = recentKeywordAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    private fun initClickListener() {
        binding.communitySearchBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.communitySearchRecentProfileDeleteAllTv.setOnClickListener {
            // 나중에 전체 삭제 기능 연결
        }

        binding.communitySearchRecentKeywordDeleteAllTv.setOnClickListener {
            // 나중에 전체 삭제 기능 연결
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}