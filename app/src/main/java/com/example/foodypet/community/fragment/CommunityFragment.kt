package com.example.foodypet.community.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.community.adapter.CommunityPostAdapter
import com.example.foodypet.community.adapter.MealInfoAdapter
import com.example.foodypet.community.model.CommunityPost
import com.example.foodypet.community.model.CommunityPostData
import com.example.foodypet.community.model.MealInfo
import com.example.foodypet.databinding.DialogMealInfoBinding
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

        setCommunityPosts(CommunityPostData.getCommunityPosts())
    }

    private fun initRecyclerView() {
        communityPostAdapter = CommunityPostAdapter(
            onMenuClick = {
                // TODO: 메뉴 버튼 클릭 시 처리
            },
            onOpenMealClick = {
                showMealInfoDialog()
            },
            onMoreClick = {
                // TODO: 더보기 클릭 시 처리
            },
            onCommentClick = {
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

    private fun showMealInfoDialog() {
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

        val mealList = listOf(
            MealInfo("닭오돌뼈", "10g"),
            MealInfo("흑돼지 치즈볼", "1봉"),
            MealInfo("플라그오프", "1개"),
            MealInfo("어거스트 슈퍼부스트", "10g"),
            MealInfo("뉴로액트", "10g")
        )

        val mealInfoAdapter = MealInfoAdapter(mealList)

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

    private fun setCommunityPosts(posts: List<CommunityPost>) {
        communityPostAdapter.setPosts(posts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.communityRv.adapter = null
        _binding = null
    }
}