package com.example.foodypet.community.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.foodypet.community.viewmodel.CommunityPostSharedViewModel
import com.example.foodypet.databinding.FragmentCommunityPostWriteBinding

class CommunityPostWriteFragment : Fragment() {

    private var _binding: FragmentCommunityPostWriteBinding? = null
    private val binding get() = _binding!!

    private val communityPostSharedViewModel: CommunityPostSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommunityPostWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListener()
    }

    private fun initClickListener() {
        binding.btnConnectMeal.setOnClickListener {
            showConnectMealBottomSheet()
        }
    }

    private fun showConnectMealBottomSheet() {
        val petItems = communityPostSharedViewModel.getCurrentPetItems()

        if (petItems.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "반려동물 정보를 불러올 수 없습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val bottomSheet = ConnectMealBottomSheet(
            petItems = petItems,
            onMealConnected = {
                // TODO: 식단 연결 성공 후 게시글 작성 화면 UI 갱신
                // 예: 연결된 식단 박스 보이기, 식단 설명 표시 등
            }
        )

        bottomSheet.show(parentFragmentManager, "ConnectMealBottomSheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}