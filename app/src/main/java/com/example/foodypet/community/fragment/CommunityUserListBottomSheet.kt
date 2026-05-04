package com.example.foodypet.community.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.community.adapter.CommunityUserListAdapter
import com.example.foodypet.community.`enum`.CommunityUserListType
import com.example.foodypet.community.model.CommunityUserListItem
import com.example.foodypet.databinding.BottomSheetCommunityUserListBinding
import com.example.foodypet.databinding.ViewCommunityProfileMoreMenuBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommunityUserListBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCommunityUserListBinding? = null
    private val binding get() = _binding!!

    private lateinit var listType: CommunityUserListType
    private lateinit var communityUserListAdapter: CommunityUserListAdapter

    private val currentItems = mutableListOf<CommunityUserListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val typeName = arguments?.getString(ARG_LIST_TYPE)
            ?: CommunityUserListType.BLOCKED.name

        listType = CommunityUserListType.valueOf(typeName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCommunityUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return

        val screenHeight = resources.displayMetrics.heightPixels
        bottomSheet.layoutParams.height = (screenHeight * 0.72).toInt()

        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTopArea()
        initRecyclerView()
        loadDummyData()
    }

    private fun initTopArea() {
        binding.communityUserListTitleTv.text = listType.title

        binding.communityUserListBackBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun initRecyclerView() {
        communityUserListAdapter = CommunityUserListAdapter(
            listType = listType,
            onFollowClick = { item ->
                // TODO: 팔로우 / 언팔로우 API 연결
            },
            onMenuClick = { anchorView, item ->
                when (listType) {
                    CommunityUserListType.BLOCKED -> {
                        showUnblockMenu(anchorView, item)
                    }

                    CommunityUserListType.FOLLOWER,
                    CommunityUserListType.FOLLOWING -> {
                        // TODO: 팔로우/팔로잉 메뉴 필요하면 여기서 처리
                    }

                    CommunityUserListType.NOTIFICATION -> {
                        // 알림은 메뉴 버튼 숨김
                    }
                }
            },
            onItemClick = { item ->
                // TODO: 유저 프로필 화면 이동
            }
        )

        binding.communityUserListRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = communityUserListAdapter
        }
    }

    private fun loadDummyData() {
        val dummyList = when (listType) {
            CommunityUserListType.BLOCKED -> getBlockedDummyUsers()
            CommunityUserListType.NOTIFICATION -> getNotificationDummyUsers()
            CommunityUserListType.FOLLOWER -> getFollowerDummyUsers()
            CommunityUserListType.FOLLOWING -> getFollowingDummyUsers()
        }

        currentItems.clear()
        currentItems.addAll(dummyList)

        communityUserListAdapter.submitList(currentItems.toList())
    }

    private fun showUnblockMenu(anchorView: View, item: CommunityUserListItem) {
        val menuBinding = ViewCommunityProfileMoreMenuBinding.inflate(layoutInflater)

        menuBinding.menuBlockTv.text = "차단 해제"
        menuBinding.menuAlarmLayout.visibility = View.GONE

        val popupWindow = PopupWindow(
            menuBinding.root,
            dpToPx(110),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            elevation = dpToPx(4).toFloat()
        }

        menuBinding.menuBlockLayout.setOnClickListener {
            popupWindow.dismiss()

            // TODO: 서버 연결 후 차단 해제 API 호출
            removeBlockedUser(item)
        }

        popupWindow.showAsDropDown(
            anchorView,
            -dpToPx(95),
            dpToPx(4)
        )
    }

    private fun removeBlockedUser(item: CommunityUserListItem) {
        currentItems.removeAll { it.userId == item.userId }
        communityUserListAdapter.submitList(currentItems.toList())
    }

    private fun getBlockedDummyUsers(): List<CommunityUserListItem> {
        return listOf(
            CommunityUserListItem(
                userId = 1L,
                profileImageResId = R.drawable.cat_1,
                nickname = "내꿈은츄르",
                petType = "고양이",
                description = "우리집냥 님 외 23명이 팔로우합니다."
            ),
            CommunityUserListItem(
                userId = 2L,
                profileImageResId = R.drawable.cat_1,
                nickname = "나비집사",
                petType = "고양이",
                description = "고양이냠 님 외 3명이 팔로우합니다."
            ),
            CommunityUserListItem(
                userId = 3L,
                profileImageResId = R.drawable.cat_1,
                nickname = "멍냥일기",
                petType = "강아지",
                description = "초코엄마 님 외 12명이 팔로우합니다."
            ),
            CommunityUserListItem(
                userId = 4L,
                profileImageResId = R.drawable.cat_1,
                nickname = "츄르수집가",
                petType = "고양이",
                description = "냥냥펀치 님 외 8명이 팔로우합니다."
            ),
            CommunityUserListItem(
                userId = 5L,
                profileImageResId = R.drawable.cat_1,
                nickname = "간식주세요",
                petType = "강아지",
                description = "보리집사 님 외 5명이 팔로우합니다."
            ),
            CommunityUserListItem(
                userId = 6L,
                profileImageResId = R.drawable.cat_1,
                nickname = "밥그릇지킴이",
                petType = "고양이",
                description = "냥이맘 님 외 16명이 팔로우합니다."
            ),
            CommunityUserListItem(
                userId = 7L,
                profileImageResId = R.drawable.cat_1,
                nickname = "꾹꾹이대장",
                petType = "고양이",
                description = "집사일기 님 외 21명이 팔로우합니다."
            ),
            CommunityUserListItem(
                userId = 8L,
                profileImageResId = R.drawable.cat_1,
                nickname = "보리산책",
                petType = "강아지",
                description = "초코엄마 님 외 10명이 팔로우합니다."
            )
        )
    }

    private fun getNotificationDummyUsers(): List<CommunityUserListItem> {
        return listOf(
            CommunityUserListItem(
                userId = 101L,
                profileImageResId = R.drawable.cat_1,
                nickname = "내꿈은츄르",
                petType = "고양이",
                description = "내꿈은츄르님이 댓글을 남겼어요.",
                isUnreadNotification = true
            ),
            CommunityUserListItem(
                userId = 102L,
                profileImageResId = R.drawable.cat_1,
                nickname = "나비집사",
                petType = "고양이",
                description = "나비집사님이 나를 팔로우하기 시작했어요.",
                isUnreadNotification = true
            ),
            CommunityUserListItem(
                userId = 103L,
                profileImageResId = R.drawable.cat_1,
                nickname = "멍냥일기",
                petType = "강아지",
                description = "멍냥일기님이 내 게시물을 좋아합니다.",
                isUnreadNotification = false
            ),
            CommunityUserListItem(
                userId = 104L,
                profileImageResId = R.drawable.cat_1,
                nickname = "냥냥펀치",
                petType = "고양이",
                description = "냥냥펀치님이 내 게시글에 댓글을 남겼어요.",
                isUnreadNotification = false
            )
        )
    }

    private fun getFollowerDummyUsers(): List<CommunityUserListItem> {
        return listOf(
            CommunityUserListItem(
                userId = 201L,
                profileImageResId = R.drawable.cat_1,
                nickname = "내꿈은츄르",
                petType = "고양이",
                description = "우리집냥 님 외 23명이 팔로우합니다.",
                isFollowing = false
            ),
            CommunityUserListItem(
                userId = 202L,
                profileImageResId = R.drawable.cat_1,
                nickname = "나비집사",
                petType = "고양이",
                description = "고양이냠 님 외 9명이 팔로우합니다.",
                isFollowing = true
            ),
            CommunityUserListItem(
                userId = 203L,
                profileImageResId = R.drawable.cat_1,
                nickname = "멍냥일기",
                petType = "강아지",
                description = "초코엄마 님 외 14명이 팔로우합니다.",
                isFollowing = false
            ),
            CommunityUserListItem(
                userId = 204L,
                profileImageResId = R.drawable.cat_1,
                nickname = "츄르수집가",
                petType = "고양이",
                description = "냥냥펀치 님 외 8명이 팔로우합니다.",
                isFollowing = true
            )
        )
    }

    private fun getFollowingDummyUsers(): List<CommunityUserListItem> {
        return listOf(
            CommunityUserListItem(
                userId = 301L,
                profileImageResId = R.drawable.cat_1,
                nickname = "냥냥펀치",
                petType = "고양이",
                description = "우리집냥 님 외 11명이 팔로우합니다.",
                isFollowing = true
            ),
            CommunityUserListItem(
                userId = 302L,
                profileImageResId = R.drawable.cat_1,
                nickname = "초코엄마",
                petType = "강아지",
                description = "보리집사 님 외 7명이 팔로우합니다.",
                isFollowing = true
            ),
            CommunityUserListItem(
                userId = 303L,
                profileImageResId = R.drawable.cat_1,
                nickname = "고양이냠",
                petType = "고양이",
                description = "내꿈은츄르 님 외 18명이 팔로우합니다.",
                isFollowing = true
            ),
            CommunityUserListItem(
                userId = 304L,
                profileImageResId = R.drawable.cat_1,
                nickname = "밥그릇지킴이",
                petType = "고양이",
                description = "냥이맘 님 외 16명이 팔로우합니다.",
                isFollowing = true
            )
        )
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_LIST_TYPE = "list_type"

        fun newInstance(listType: CommunityUserListType): CommunityUserListBottomSheet {
            return CommunityUserListBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_LIST_TYPE, listType.name)
                }
            }
        }
    }
}