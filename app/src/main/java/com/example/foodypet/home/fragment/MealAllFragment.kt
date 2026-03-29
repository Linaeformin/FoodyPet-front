package com.example.foodypet.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.MealAdapter
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMealAllBinding
import com.example.foodypet.home.model.MealItem
import androidx.core.content.res.ResourcesCompat

class MealAllFragment : Fragment() {

    private var _binding: FragmentMealAllBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealAdapter: MealAdapter

    private val unfedMealList = mutableListOf<MealItem>()
    private val fedMealList = mutableListOf<MealItem>()

    private var selectedTab = MealTabType.UNFED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initRecyclerView()
        initMealTabs()
        initClickListener()

        // 임시 더미 데이터
        val mockMealList = listOf(
            MealItem(
                mealId = 1L,
                time = "15:00",
                content = "흑돼지 치즈볼 1개, 닭오돌뼈 10g, 플라그오프, 어거스틴 슈퍼부스트, 뉴로액트, 도란도란 단호박",
                isFed = false
            ),
            MealItem(
                mealId = 2L,
                time = "18:00",
                content = "닭가슴살 20g, 영양제 1개",
                isFed = false
            ),
            MealItem(
                mealId = 3L,
                time = "12:00",
                content = "플라그오프, 뉴로액트",
                isFed = false
            ),
            MealItem(
                mealId = 4L,
                time = "15:00",
                content = "흑돼지 치즈볼 1개, 닭오돌뼈 10g, 플라그오프, 어거스틴 슈퍼부스트, 뉴로액트, 도란도란 단호박",
                isFed = false
            ),
            MealItem(
                mealId = 5L,
                time = "18:00",
                content = "닭가슴살 20g, 영양제 1개",
                isFed = false
            ),
            MealItem(
                mealId = 6L,
                time = "12:00",
                content = "플라그오프, 뉴로액트",
                isFed = false
            )
        )

        setMealData(mockMealList)
    }

    private fun initView() {
        binding.mealOwnerTv.text = "랑이의 식단"
        binding.mealDateTv.text = "2026.03.30"
    }

    private fun initRecyclerView() {
        mealAdapter = MealAdapter()

        binding.mealListRv.apply {
            adapter = mealAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initMealTabs() {
        binding.mealUnfedTabTv.setOnClickListener {
            if (selectedTab == MealTabType.UNFED) return@setOnClickListener

            selectedTab = MealTabType.UNFED
            updateMealTabUi()
            mealAdapter.submitList(unfedMealList.toList())
        }

        binding.mealFedTabTv.setOnClickListener {
            if (selectedTab == MealTabType.FED) return@setOnClickListener

            selectedTab = MealTabType.FED
            updateMealTabUi()
            mealAdapter.submitList(fedMealList.toList())
        }

        updateMealTabUi()
    }

    private fun updateMealTabUi() {
        when (selectedTab) {
            MealTabType.UNFED -> {
                binding.mealUnfedTabTv.setBackgroundResource(R.drawable.bg_meal_tab_selected)
                binding.mealUnfedTabTv.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
                binding.mealUnfedTabTv.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.scdream_medium)

                binding.mealFedTabTv.setBackgroundResource(R.drawable.bg_meal_tab_unselected)
                binding.mealFedTabTv.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.black)
                )
                binding.mealFedTabTv.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.scdream_light)
            }

            MealTabType.FED -> {
                binding.mealFedTabTv.setBackgroundResource(R.drawable.bg_meal_tab_selected)
                binding.mealFedTabTv.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
                binding.mealFedTabTv.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.scdream_medium)

                binding.mealUnfedTabTv.setBackgroundResource(R.drawable.bg_meal_tab_unselected)
                binding.mealUnfedTabTv.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.black)
                )
                binding.mealUnfedTabTv.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.scdream_light)
            }
        }
    }

    private fun initClickListener() {
        binding.mealBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.mealEditTv.setOnClickListener {
            // 식단 수정 화면 이동
        }

        binding.mealRecommendTv.setOnClickListener {
            // 이후 식단 추천 받기 화면 이동
        }
    }

    private fun setMealData(allMealList: List<MealItem>) {
        unfedMealList.clear()
        fedMealList.clear()

        unfedMealList.addAll(allMealList.filter { !it.isFed })
        fedMealList.addAll(allMealList.filter { it.isFed })

        binding.mealUnfedTabTv.text = "미급여"
        binding.mealFedTabTv.text = "급여"

        when (selectedTab) {
            MealTabType.UNFED -> mealAdapter.submitList(unfedMealList.toList())
            MealTabType.FED -> mealAdapter.submitList(fedMealList.toList())
        }

        updateMealTabUi()
    }

    fun updateMealListFromServer(responseMealList: List<MealItem>) {
        setMealData(responseMealList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private enum class MealTabType {
        UNFED, FED
    }
}