package com.example.foodypet.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.home.adapter.MealAdapter
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMealRecommendBinding
import com.example.foodypet.home.model.MealItem

class MealRecommendFragment : Fragment() {

    private var _binding: FragmentMealRecommendBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealAdapter: MealAdapter

    private var isTimeToggleOn = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initView()
        initClickListener()
        loadDummyData()
    }

    private fun initAdapter() {
        mealAdapter = MealAdapter()

        binding.mealRecommendRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealAdapter
            itemAnimator = null
            isNestedScrollingEnabled = false
        }
    }

    private fun initView() {
        binding.mealTitleTv.text = "식단 추천 받기"
        binding.mealReflectTitleTv.text = "반영 항목"
        binding.mealRecommendTitleTv.text = "랑이의 추천 식단"

        binding.mealGenderTv.text = "성별"
        binding.mealWeightTv.text = "종"
        binding.mealNeuteredTv.text = "중성화"
        binding.mealAgeTv.text = "나이"
        binding.mealFeedCountTv.text = "하루 6회 급여"

        updateTimeToggleUi()
    }

    private fun initClickListener() {
        binding.mealBackIv.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.timeToggleLayout.setOnClickListener {
            isTimeToggleOn = !isTimeToggleOn
            updateTimeToggleUi()
            loadDummyData()
        }
    }

    private fun updateTimeToggleUi() {
        if (isTimeToggleOn) {
            binding.timeToggleIv.setImageResource(R.drawable.icon_toggle_apply)
        } else {
            binding.timeToggleIv.setImageResource(R.drawable.icon_toggle_unapply)
        }
    }

    private fun loadDummyData() {
        val dummyList = if (isTimeToggleOn) {
            listOf(
                MealItem(
                    mealId = 1L,
                    time = "13:00",
                    content = "흑돼지 치즈볼 1개, 닭오돌뼈 10g, 플라그오프, 어거스틴 슈퍼부스트, 뉴로액트, 도란도란 단호박",
                    isFed = false
                ),
                MealItem(
                    mealId = 2L,
                    time = "15:00",
                    content = "닭가슴살볼 2개, 북어트릿 5g, 오메가3, 유산균, 브로콜리 소량",
                    isFed = false
                ),
                MealItem(
                    mealId = 3L,
                    time = "17:00",
                    content = "연어 큐브 20g, 고구마 30g, 플라그오프, 뉴로액트",
                    isFed = false
                ),
                MealItem(
                    mealId = 4L,
                    time = "19:00",
                    content = "오리안심 15g, 단호박 20g, 슈퍼부스트, 유산균",
                    isFed = false
                ),
                MealItem(
                    mealId = 5L,
                    time = "21:00",
                    content = "소고기 패티 1개, 파프리카 소량, 오메가3, 도란도란 단호박",
                    isFed = false
                ),
                MealItem(
                    mealId = 6L,
                    time = "23:00",
                    content = "흰살생선 트릿 2개, 블루베리 소량, 플라그오프, 유산균",
                    isFed = false
                )
            )
        } else {
            listOf(
                MealItem(
                    mealId = 1L,
                    time = "1회",
                    content = "흑돼지 치즈볼 1개, 닭오돌뼈 10g, 플라그오프, 어거스틴 슈퍼부스트, 뉴로액트, 도란도란 단호박",
                    isFed = false
                ),
                MealItem(
                    mealId = 2L,
                    time = "2회",
                    content = "닭가슴살볼 2개, 북어트릿 5g, 오메가3, 유산균, 브로콜리 소량",
                    isFed = false
                ),
                MealItem(
                    mealId = 3L,
                    time = "3회",
                    content = "연어 큐브 20g, 고구마 30g, 플라그오프, 뉴로액트",
                    isFed = false
                ),
                MealItem(
                    mealId = 4L,
                    time = "4회",
                    content = "오리안심 15g, 단호박 20g, 슈퍼부스트, 유산균",
                    isFed = false
                ),
                MealItem(
                    mealId = 5L,
                    time = "5회",
                    content = "소고기 패티 1개, 파프리카 소량, 오메가3, 도란도란 단호박",
                    isFed = false
                ),
                MealItem(
                    mealId = 6L,
                    time = "6회",
                    content = "흰살생선 트릿 2개, 블루베리 소량, 플라그오프, 유산균",
                    isFed = false
                )
            )
        }

        mealAdapter.submitList(dummyList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}