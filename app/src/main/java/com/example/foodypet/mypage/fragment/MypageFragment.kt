package com.example.foodypet.mypage.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodypet.databinding.FragmentMypageBinding

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
    }

    private fun setLogoutUnderline() {
        binding.mypageLogoutBtn.paintFlags =
            binding.mypageLogoutBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun setClickListeners() {
        binding.mypagePetManageBtn.setOnClickListener {
            // TODO: 반려동물 관리 화면으로 이동
        }

        binding.mypageMealRecordManageBtn.setOnClickListener {
            // TODO: 밥 일기 관리 화면으로 이동
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
            // TODO: 로그아웃 처리
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}