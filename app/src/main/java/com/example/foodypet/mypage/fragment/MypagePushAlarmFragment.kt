package com.example.foodypet.mypage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMypagePushAlarmBinding

class MypagePushAlarmFragment : Fragment() {

    private var _binding: FragmentMypagePushAlarmBinding? = null
    private val binding get() = _binding!!

    private var isCommunityAlarmOn = true
    private var isMealAlarmOn = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypagePushAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickListeners()
    }

    private fun initView() {
        updateCommunityAlarmToggle()
        updateMealAlarmToggle()
    }

    private fun initClickListeners() {
        binding.mypagePushAlarmBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.mypageCommunityAlarmArea.setOnClickListener {
            isCommunityAlarmOn = !isCommunityAlarmOn
            updateCommunityAlarmToggle()
        }

        binding.mypageCommunityAlarmToggleIv.setOnClickListener {
            isCommunityAlarmOn = !isCommunityAlarmOn
            updateCommunityAlarmToggle()
        }

        binding.mypageMealAlarmArea.setOnClickListener {
            isMealAlarmOn = !isMealAlarmOn
            updateMealAlarmToggle()
        }

        binding.mypageMealAlarmToggleIv.setOnClickListener {
            isMealAlarmOn = !isMealAlarmOn
            updateMealAlarmToggle()
        }

        binding.mypagePushAlarmSaveBtn.setOnClickListener {
            savePushAlarmSetting()
        }
    }

    private fun updateCommunityAlarmToggle() {
        val toggleIcon = if (isCommunityAlarmOn) {
            R.drawable.icon_toggle_apply
        } else {
            R.drawable.icon_toggle_unapply
        }

        binding.mypageCommunityAlarmToggleIv.setImageResource(toggleIcon)
    }

    private fun updateMealAlarmToggle() {
        val toggleIcon = if (isMealAlarmOn) {
            R.drawable.icon_toggle_apply
        } else {
            R.drawable.icon_toggle_unapply
        }

        binding.mypageMealAlarmToggleIv.setImageResource(toggleIcon)
    }

    private fun savePushAlarmSetting() {
        // TODO: 서버 API 연결 후 푸시 알림 설정 저장 요청 보내기
        // 예시:
        // viewModel.savePushAlarmSetting(
        //     communityAlarm = isCommunityAlarmOn,
        //     mealAlarm = isMealAlarmOn
        // )

        Toast.makeText(requireContext(), "푸시 알림 설정이 저장되었습니다.", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}