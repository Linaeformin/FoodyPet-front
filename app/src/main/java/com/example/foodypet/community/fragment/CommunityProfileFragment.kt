package com.example.foodypet.community.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodypet.R
import com.example.foodypet.community.adapter.CommunityProfilePostAdapter
import com.example.foodypet.community.model.CommunityProfilePost
import com.example.foodypet.databinding.FragmentCommunityProfileBinding
import android.view.Gravity
import android.widget.PopupWindow
import com.example.foodypet.community.enum.CommunityUserListType
import com.example.foodypet.databinding.ViewCommunityProfileMoreMenuBinding

class CommunityProfileFragment : Fragment() {

    private var _binding: FragmentCommunityProfileBinding? = null
    private val binding get() = _binding!!

    private var isMyProfile: Boolean = false

    private lateinit var profilePostAdapter: CommunityProfilePostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isMyProfile = arguments?.getBoolean(ARG_IS_MY_PROFILE) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTopArea()
        initProfileMode()
        initFollowCountClick()
        initTabs()
        initRecyclerView()
    }

    private fun showMoreMenu() {
        val menuBinding = ViewCommunityProfileMoreMenuBinding.inflate(layoutInflater)

        menuBinding.root.layoutParams = ViewGroup.LayoutParams(
            dpToPx(110),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (isMyProfile) {
            menuBinding.menuBlockTv.text = "차단한 사용자"
            menuBinding.menuAlarmLayout.visibility = View.VISIBLE
        } else {
            menuBinding.menuBlockTv.text = "차단하기"
            menuBinding.menuAlarmLayout.visibility = View.GONE
        }

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

        menuBinding.menuBlockLayout.setOnClickListener {
            popupWindow.dismiss()

            if (isMyProfile) {
                CommunityUserListBottomSheet.newInstance(
                    CommunityUserListType.BLOCKED
                ).show(parentFragmentManager, "CommunityBlockedBottomSheet")
            } else {
                blockUser()
            }
        }

        menuBinding.menuAlarmLayout.setOnClickListener {
            popupWindow.dismiss()

            CommunityUserListBottomSheet.newInstance(
                CommunityUserListType.NOTIFICATION
            ).show(parentFragmentManager, "CommunityNotificationBottomSheet")
        }

        popupWindow.showAsDropDown(
            binding.mealMoreIv,
            -dpToPx(95),
            dpToPx(6)
        )
    }

    private fun blockUser() {
        // TODO: 서버 연결 후 차단하기 API 호출

        parentFragmentManager.popBackStack()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun maskUserId(userId: String): String {
        if (userId.length <= 4) return userId

        val visiblePart = userId.take(4)
        val maskedPart = "*".repeat(userId.length - 4)

        return visiblePart + maskedPart
    }

    private fun initTopArea() {
        binding.mealTitleTv.text = "nyamnyam"

        binding.mealBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.mealMoreIv.visibility = View.VISIBLE

        binding.mealMoreIv.setOnClickListener {
            showMoreMenu()
        }
    }

    private fun initProfileMode() {
        if (isMyProfile) {
            setMyProfileUi()
        } else {
            setOtherProfileUi()
        }
    }

    private fun setMyProfileUi() {
        binding.mealTitleTv.text = "nyamnyam"

        binding.communityProfileEditBtn.text = "프로필 편집"
        binding.communityProfileLinkBtn.text = "프로필 링크"

        binding.communityProfileEditBtn.setOnClickListener {
            // TODO: 프로필 편집 화면으로 이동
        }

        binding.communityProfileLinkBtn.setOnClickListener {
            // TODO: 프로필 링크 복사 또는 공유
        }
    }

    private fun setOtherProfileUi() {
        binding.mealTitleTv.text = maskUserId(binding.mealTitleTv.text.toString())

        binding.communityProfileEditBtn.text = "팔로우"
        binding.communityProfileLinkBtn.text = "프로필 링크"

        binding.communityProfileEditBtn.setOnClickListener {
            toggleFollow()
        }

        binding.communityProfileLinkBtn.setOnClickListener {
            // TODO: 프로필 링크 복사 또는 공유
        }
    }

    private fun initFollowCountClick() {
        binding.communityProfileFollowingTv.setOnClickListener {
            CommunityUserListBottomSheet.newInstance(
                CommunityUserListType.FOLLOWING
            ).show(parentFragmentManager, "CommunityFollowingBottomSheet")
        }

        binding.communityProfileFollowerTv.setOnClickListener {
            CommunityUserListBottomSheet.newInstance(
                CommunityUserListType.FOLLOWER
            ).show(parentFragmentManager, "CommunityFollowerBottomSheet")
        }
    }

    private fun toggleFollow() {
        val isFollowing = binding.communityProfileEditBtn.text.toString() == "팔로잉"

        binding.communityProfileEditBtn.text = if (isFollowing) {
            "팔로우"
        } else {
            "팔로잉"
        }
    }

    private fun initTabs() {
        if (isMyProfile) {
            binding.communityProfileBookmarkTab.visibility = View.VISIBLE
        } else {
            binding.communityProfileBookmarkTab.visibility = View.GONE
        }

        selectTab(ProfileTab.HOME)

        binding.communityProfileHomeTab.setOnClickListener {
            selectTab(ProfileTab.HOME)
            // TODO: 게시글 목록 조회
        }

        binding.communityProfileHeartTab.setOnClickListener {
            selectTab(ProfileTab.HEART)
            // TODO: 좋아요한 게시글 목록 조회
        }

        binding.communityProfileBookmarkTab.setOnClickListener {
            if (isMyProfile) {
                selectTab(ProfileTab.BOOKMARK)
                // TODO: 북마크한 게시글 목록 조회
            }
        }
    }

    private fun selectTab(tab: ProfileTab) {
        val selectedTab = if (!isMyProfile && tab == ProfileTab.BOOKMARK) {
            ProfileTab.HOME
        } else {
            tab
        }

        binding.communityProfileHomeTab.setBackgroundResource(
            if (selectedTab == ProfileTab.HOME) {
                R.drawable.bg_community_tab_select
            } else {
                R.drawable.bg_community_tab_unselect
            }
        )

        binding.communityProfileHeartTab.setBackgroundResource(
            if (selectedTab == ProfileTab.HEART) {
                R.drawable.bg_community_tab_select
            } else {
                R.drawable.bg_community_tab_unselect
            }
        )

        binding.communityProfileBookmarkTab.setBackgroundResource(
            if (selectedTab == ProfileTab.BOOKMARK) {
                R.drawable.bg_community_tab_select
            } else {
                R.drawable.bg_community_tab_unselect
            }
        )
    }

    private fun initRecyclerView() {
        profilePostAdapter = CommunityProfilePostAdapter { post ->
            // TODO: 게시글 상세 화면으로 이동
        }

        val spanCount = if (isMyProfile) {
            3
        } else {
            2
        }

        binding.communityProfilePostRv.apply {
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            adapter = profilePostAdapter
        }

        profilePostAdapter.submitList(getDummyPosts())
    }

    private fun getDummyPosts(): List<CommunityProfilePost> {
        return listOf(
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal),
            CommunityProfilePost(R.drawable.img_community_meal)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private enum class ProfileTab {
        HOME,
        HEART,
        BOOKMARK
    }

    companion object {
        private const val ARG_IS_MY_PROFILE = "isMyProfile"

        fun newInstance(isMyProfile: Boolean): CommunityProfileFragment {
            return CommunityProfileFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_MY_PROFILE, isMyProfile)
                }
            }
        }
    }
}