package com.example.foodypet.community.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.community.adapter.CommunitySearchPostAdapter
import com.example.foodypet.community.adapter.CommunitySearchProfileAdapter
import com.example.foodypet.community.model.CommunitySearchPost
import com.example.foodypet.community.model.CommunitySearchProfile
import com.example.foodypet.databinding.FragmentCommunitySearchResultBinding
import android.text.Editable
import android.text.TextWatcher

class CommunitySearchResultFragment : Fragment() {

    private var _binding: FragmentCommunitySearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var communitySearchProfileAdapter: CommunitySearchProfileAdapter
    private lateinit var communitySearchPostAdapter: CommunitySearchPostAdapter

    private var selectedTab: CommunitySearchTab = CommunitySearchTab.ACCOUNT
    private var currentKeyword: String = ""
    private var isInitializingKeyword = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunitySearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentKeyword = arguments?.getString(CommunitySearchFragment.SEARCH_KEYWORD_KEY).orEmpty()

        initAdapter()
        initKeyword()
        initClickListener()
        initSearchEditText()
        setAccountTab()
    }

    private fun initAdapter() {
        communitySearchProfileAdapter = CommunitySearchProfileAdapter()
        communitySearchPostAdapter = CommunitySearchPostAdapter()
    }

    private fun initKeyword() {
        isInitializingKeyword = true

        binding.communitySearchEt.setText(currentKeyword)
        binding.communitySearchEt.setSelection(currentKeyword.length)

        isInitializingKeyword = false
    }

    private fun initClickListener() {
        binding.communitySearchBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.communitySearchAccountTabLayout.setOnClickListener {
            if (selectedTab != CommunitySearchTab.ACCOUNT) {
                setAccountTab()
            }
        }

        binding.communitySearchPostTabLayout.setOnClickListener {
            if (selectedTab != CommunitySearchTab.POST) {
                setPostTab()
            }
        }
    }

    private fun initSearchEditText() {
        binding.communitySearchEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isInitializingKeyword) {
                moveToSearchFragment(binding.communitySearchEt.text.toString().trim())
            }
        }

        binding.communitySearchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!isInitializingKeyword && s.toString() != currentKeyword) {
                    moveToSearchFragment(s.toString().trim())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun setAccountTab() {
        selectedTab = CommunitySearchTab.ACCOUNT

        binding.communitySearchAccountTabLine.isVisible = true
        binding.communitySearchPostTabLine.isVisible = false

        binding.communitySearchAccountTabTv.setTextColor(
            requireContext().getColor(R.color.black)
        )
        binding.communitySearchPostTabTv.setTextColor(
            requireContext().getColor(R.color.dark_gray)
        )

        binding.communitySearchResultRv.layoutManager = LinearLayoutManager(requireContext())
        binding.communitySearchResultRv.adapter = communitySearchProfileAdapter

        searchCommunity(currentKeyword)
    }

    private fun setPostTab() {
        selectedTab = CommunitySearchTab.POST

        binding.communitySearchAccountTabLine.isVisible = false
        binding.communitySearchPostTabLine.isVisible = true

        binding.communitySearchAccountTabTv.setTextColor(
            requireContext().getColor(R.color.dark_gray)
        )
        binding.communitySearchPostTabTv.setTextColor(
            requireContext().getColor(R.color.black)
        )

        binding.communitySearchResultRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.communitySearchResultRv.adapter = communitySearchPostAdapter

        searchCommunity(currentKeyword)
    }

    private fun searchCommunity(keyword: String) {
        when (selectedTab) {
            CommunitySearchTab.ACCOUNT -> {
                // TODO: 계정 검색 API 연결
                communitySearchProfileAdapter.submitList(getDummyProfiles())
            }

            CommunitySearchTab.POST -> {
                // TODO: 게시글 검색 API 연결
                communitySearchPostAdapter.submitList(getDummyPosts())
            }
        }
    }

    private fun moveToSearchFragment(keyword: String) {
        val fragment = CommunitySearchFragment.newInstance(keyword)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getDummyProfiles(): List<CommunitySearchProfile> {
        return listOf(
            CommunitySearchProfile(
                profileImage = R.drawable.cat_2,
                nickname = "내꿈은츄르",
                category = "고양이",
                description = "우긴집냥 님 외 23명이 팔로우합니다."
            ),
            CommunitySearchProfile(
                profileImage = R.drawable.cat_1,
                nickname = "내꿈은츄르",
                category = "고양이",
                description = "우긴집냥 님 외 23명이 팔로우합니다."
            ),
            CommunitySearchProfile(
                profileImage = R.drawable.cat_2,
                nickname = "내꿈은츄르",
                category = "고양이",
                description = "우긴집냥 님 외 23명이 팔로우합니다."
            )
        )
    }

    private fun getDummyPosts(): List<CommunitySearchPost> {
        return listOf(
            CommunitySearchPost(R.drawable.img_community_meal),
            CommunitySearchPost(R.drawable.img_meal),
            CommunitySearchPost(R.drawable.img_community_meal),
            CommunitySearchPost(R.drawable.img_meal),
            CommunitySearchPost(R.drawable.img_community_meal),
            CommunitySearchPost(R.drawable.img_meal)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.communitySearchResultRv.adapter = null
        _binding = null
    }
}