package com.example.foodypet.mypage.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMypageBinding
import com.example.foodypet.ui.mypage.LogoutDialog

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLogoutUnderline()
        setClickListeners()

        // TODO: 나중에 서버 응답 데이터로 교체
        val petList = emptyList<String>()

        setPetPreview(petList)
    }

    private fun setPetPreview(petList: List<String>) {
        if (petList.isEmpty()) {
            binding.mypagePetImgArea.visibility = View.GONE
            binding.mypagePetMoreCountTv.text = "반려동물 등록하기"
            binding.mypagePetMoreCountTv.setTextColor(
                requireContext().getColor(R.color.dark_gray)
            )
        } else {
            binding.mypagePetImgArea.visibility = View.VISIBLE

            binding.mypagePetMoreCountTv.setTextColor(
                requireContext().getColor(R.color.black)
            )

            binding.mypagePetMoreCountTv.text =
                if (petList.size > 3) {
                    "+ ${petList.size - 3}"
                } else {
                    ""
                }
        }
    }

    private fun setLogoutUnderline() {
        binding.mypageLogoutBtn.paintFlags =
            binding.mypageLogoutBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun setClickListeners() {
        binding.mypagePetManageBtn.setOnClickListener {
            moveToMyPetFragment()
        }

        binding.mypagePetMoreCountTv.setOnClickListener {
            moveToMyPetFragment()
        }

        binding.mypageMealRecordManageBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MypageMealDiaryListFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.mypagePushAlarmBtn.setOnClickListener {
            // TODO: 푸시 알림 설정 화면으로 이동
        }

        binding.mypageInquiryBtn.setOnClickListener {
            // TODO: 문의하기 화면으로 이동
        }

        binding.mypagePasswordChangeBtn.setOnClickListener {
            // TODO: 비밀번호 변경 화면으로 이동
        }

        binding.mypageLogoutBtn.setOnClickListener {
            LogoutDialog().show(parentFragmentManager, "LogoutDialog")
        }
    }

    private fun moveToMyPetFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MyPetFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}