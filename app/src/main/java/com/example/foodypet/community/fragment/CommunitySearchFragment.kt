package com.example.foodypet.community.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.community.adapter.CommunitySearchResultAdapter
import com.example.foodypet.community.adapter.RecentKeywordAdapter
import com.example.foodypet.community.adapter.RecentProfileAdapter
import com.example.foodypet.community.model.CommunitySearchResult
import com.example.foodypet.community.model.RecentKeyword
import com.example.foodypet.community.model.RecentProfile
import com.example.foodypet.databinding.FragmentCommunitySearchBinding

class CommunitySearchFragment : Fragment() {

    private var _binding: FragmentCommunitySearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var recentProfileAdapter: RecentProfileAdapter
    private lateinit var recentKeywordAdapter: RecentKeywordAdapter
    private lateinit var searchResultAdapter: CommunitySearchResultAdapter

    private val dummyRecentProfileList = listOf(
        RecentProfile(
            id = 1,
            nickname = "초코",
            profileImageRes = R.drawable.cat_1
        ),
        RecentProfile(
            id = 2,
            nickname = "몽이",
            profileImageRes = R.drawable.cat_2
        ),
        RecentProfile(
            id = 3,
            nickname = "나비",
            profileImageRes = R.drawable.dog_1
        ),
        RecentProfile(
            id = 4,
            nickname = "콩이",
            profileImageRes = R.drawable.dog_2
        ),
        RecentProfile(
            id = 5,
            nickname = "보리",
            profileImageRes = R.drawable.cat_2
        )
    )

    private val dummyRecentKeywordList = listOf(
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

    private val dummySearchResultList = listOf(
        CommunitySearchResult.Keyword(
            keyword = "삼계탕"
        ),
        CommunitySearchResult.Keyword(
            keyword = "삼계탕 레시피"
        ),
        CommunitySearchResult.Keyword(
            keyword = "고양이 삼계탕"
        ),
        CommunitySearchResult.Profile(
            profileImageRes = R.drawable.cat_1,
            nickname = "내꿈은츄르",
            petType = "고양이",
            description = "우리집냥 님 외 23명이 팔로우합니다."
        ),
        CommunitySearchResult.Profile(
            profileImageRes = R.drawable.cat_2,
            nickname = "내꿈은츄르",
            petType = "고양이",
            description = "우리집냥 님 외 23명이 팔로우합니다."
        ),
        CommunitySearchResult.Profile(
            profileImageRes = R.drawable.cat_1,
            nickname = "내꿈은츄르",
            petType = "고양이",
            description = "우리집냥 님 외 23명이 팔로우합니다."
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

        initRecentRecyclerView()
        initSearchResultRecyclerView()
        initClickListener()
        initSearchEditText()
    }

    private fun initRecentRecyclerView() {
        recentProfileAdapter = RecentProfileAdapter(dummyRecentProfileList)

        binding.communitySearchRecentProfileRv.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = recentProfileAdapter
        }

        recentKeywordAdapter = RecentKeywordAdapter(dummyRecentKeywordList)

        binding.communitySearchRecentKeywordRv.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = recentKeywordAdapter
        }
    }

    private fun initSearchResultRecyclerView() {
        searchResultAdapter = CommunitySearchResultAdapter(emptyList())

        binding.communitySearchResultRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchResultAdapter
        }
    }

    private fun initClickListener() {
        binding.communitySearchBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.communitySearchRecentProfileDeleteAllTv.setOnClickListener {
            // TODO: 최근 프로필 전체 삭제
        }

        binding.communitySearchRecentKeywordDeleteAllTv.setOnClickListener {
            // TODO: 최근 검색어 전체 삭제
        }
    }

    private fun initSearchEditText() {
        binding.communitySearchEt.requestFocus()
        showKeyboard()

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
                val keyword = s.toString().trim()

                if (keyword.isEmpty()) {
                    showRecentSearchView()
                } else {
                    showSearchResultView()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun showRecentSearchView() {
        binding.communitySearchScrollView.visibility = View.VISIBLE
        binding.communitySearchResultRv.visibility = View.GONE
        searchResultAdapter.setSearchResults(emptyList())
    }

    private fun showSearchResultView() {
        binding.communitySearchScrollView.visibility = View.GONE
        binding.communitySearchResultRv.visibility = View.VISIBLE
        searchResultAdapter.setSearchResults(dummySearchResultList)
    }

    private fun showKeyboard() {
        binding.communitySearchEt.post {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.showSoftInput(
                binding.communitySearchEt,
                InputMethodManager.SHOW_IMPLICIT
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.communitySearchRecentProfileRv.adapter = null
        binding.communitySearchRecentKeywordRv.adapter = null
        binding.communitySearchResultRv.adapter = null

        _binding = null
    }
}