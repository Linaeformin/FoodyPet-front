package com.example.foodypet.home.fragment

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMealEditorBinding
import com.example.foodypet.home.adapter.MealPagerAdapter
import com.example.foodypet.home.model.FoodUiModel
import com.example.foodypet.home.model.MealPageUiModel

class MealEditorFragment : Fragment(R.layout.fragment_meal_editor) {

    private var _binding: FragmentMealEditorBinding? = null
    private val binding get() = _binding!!

    private var isTimeMode = true
    private var currentPage = 0
    private var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    private val editorMode: MealEditorMode
        get() {
            val modeName = arguments?.getString(ARG_EDITOR_MODE) ?: MealEditorMode.CREATE.name
            return MealEditorMode.valueOf(modeName)
        }

    private val dummyInventory = listOf(
        "도란도란 단호박",
        "도란도란",
        "도란도란 우유",
        "닭오돌뼈",
        "흑돼지 치즈볼",
        "플라그오프",
        "어거스틴 슈퍼부스트",
        "뉴로액트"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMealEditorBinding.bind(view)

        applyModeUi()
        initHeader()
        initPagerCallback()
        setupPager(isTimeMode, currentPage)
        binding.mealViewPager.isUserInputEnabled = true
        back()
        initClickListeners()
        loadInitialData()
    }

    private fun applyModeUi() {
        binding.mealTitleTv.text = when (editorMode) {
            MealEditorMode.CREATE -> "식단 직접 등록하기"
            MealEditorMode.EDIT -> "식단 수정하기"
        }
    }

    private fun initHeader() {
        updateToggleImage()

        binding.timeToggleIv.setOnClickListener {
            currentPage = binding.mealViewPager.currentItem
            isTimeMode = !isTimeMode
            updateToggleImage()
            setupPager(isTimeMode, currentPage)
        }
    }

    private fun updateToggleImage() {
        if (isTimeMode) {
            binding.timeToggleIv.setImageResource(R.drawable.icon_toggle_apply)
        } else {
            binding.timeToggleIv.setImageResource(R.drawable.icon_toggle_unapply)
        }
    }

    private fun initPagerCallback() {
        pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                updateIndicator(position)
                updateBottomButtonText(position)
            }
        }

        binding.mealViewPager.registerOnPageChangeCallback(pageChangeCallback!!)
    }

    private fun setupPager(isTimeMode: Boolean, pageToMove: Int = 0) {
        val pages = if (isTimeMode) {
            listOf(
                MealPageUiModel("13:00", dummyFoods()),
                MealPageUiModel("15:00", dummyFoods()),
                MealPageUiModel("18:00", dummyFoods()),
                MealPageUiModel("20:00", dummyFoods()),
                MealPageUiModel("22:00", dummyFoods()),
                MealPageUiModel("23:30", dummyFoods())
            )
        } else {
            listOf(
                MealPageUiModel("1회", dummyFoods()),
                MealPageUiModel("2회", dummyFoods()),
                MealPageUiModel("3회", dummyFoods()),
                MealPageUiModel("4회", dummyFoods()),
                MealPageUiModel("5회", dummyFoods()),
                MealPageUiModel("6회", dummyFoods())
            )
        }

        binding.mealViewPager.adapter = MealPagerAdapter(
            pages = pages,
            inventoryItems = dummyInventory,
            isTimeMode = isTimeMode
        )

        setupIndicator(pages.size)

        val safePosition = pageToMove.coerceIn(0, pages.lastIndex)
        binding.mealViewPager.setCurrentItem(safePosition, false)
        currentPage = safePosition
        updateIndicator(safePosition)
        updateBottomButtonText(safePosition)
    }

    private fun updateBottomButtonText(position: Int) {
        val lastIndex = (binding.mealViewPager.adapter?.itemCount ?: 0) - 1
        val isLast = position == lastIndex

        binding.btnEditAnalyze.text = when {
            isLast && editorMode == MealEditorMode.CREATE -> "저장 및 분석하기"
            isLast && editorMode == MealEditorMode.EDIT -> "수정 및 분석하기"
            !isLast && editorMode == MealEditorMode.CREATE -> "저장 완료"
            else -> "수정 완료"
        }
    }

    private fun initClickListeners() {
        binding.btnEditAnalyze.setOnClickListener {
            if (isLastPage()) {
                when (editorMode) {
                    MealEditorMode.CREATE -> createMeal()
                    MealEditorMode.EDIT -> updateMeal()
                }
                moveToAnalyze()
            } else {
                when (editorMode) {
                    MealEditorMode.CREATE -> saveDraftPage()
                    MealEditorMode.EDIT -> saveEditedPage()
                }
            }
        }

        binding.btnOnlyAnalyze.setOnClickListener {
            moveToAnalyze()
        }
    }

    private fun createMeal() {
        // TODO: 직접 등록 API 호출
    }

    private fun updateMeal() {
        // TODO: 수정 API 호출
    }

    private fun saveDraftPage() {
        // TODO: 등록 중간 저장 or 로컬 상태 저장
    }

    private fun saveEditedPage() {
        // TODO: 수정 중간 저장 or 로컬 상태 저장
    }

    private fun moveToAnalyze() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AnalyzeMealFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun isLastPage(): Boolean {
        val lastIndex = (binding.mealViewPager.adapter?.itemCount ?: 0) - 1
        return binding.mealViewPager.currentItem == lastIndex
    }

    private fun loadInitialData() {
        when (editorMode) {
            MealEditorMode.CREATE -> {
                // 빈 데이터 세팅
            }
            MealEditorMode.EDIT -> {
                // TODO: 기존 저장 식단 조회 후 prefill
            }
        }
    }

    private fun dummyFoods(): List<FoodUiModel> {
        return listOf(
            FoodUiModel("닭오돌뼈", "10", "g"),
            FoodUiModel("흑돼지 치즈볼", "1", "개"),
            FoodUiModel("플라그오프", "1", "봉"),
            FoodUiModel("어거스틴 슈퍼부스트", "10", "ml"),
            FoodUiModel("뉴로액트", "10", "g"),
            FoodUiModel("도란도", "10", "g"),
            FoodUiModel("", "", "g")
        )
    }

    private fun setupIndicator(count: Int) {
        binding.indicatorLayout.removeAllViews()

        repeat(count) {
            val dot = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(11.dp, 11.dp).apply {
                    marginStart = 6.dp
                    marginEnd = 6.dp
                }
                setImageResource(R.drawable.indicator_unselect)
            }
            binding.indicatorLayout.addView(dot)
        }
    }

    private fun updateIndicator(selectedPosition: Int) {
        for (i in 0 until binding.indicatorLayout.childCount) {
            val dot = binding.indicatorLayout.getChildAt(i) as ImageView
            if (i == selectedPosition) {
                dot.setImageResource(R.drawable.indicator_select)
            } else {
                dot.setImageResource(R.drawable.indicator_unselect)
            }
        }
    }

    private fun back() {
        binding.mealBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onDestroyView() {
        pageChangeCallback?.let {
            binding.mealViewPager.unregisterOnPageChangeCallback(it)
        }
        pageChangeCallback = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_EDITOR_MODE = "editor_mode"

        fun newInstance(mode: MealEditorMode): MealEditorFragment {
            return MealEditorFragment().apply {
                arguments = bundleOf(ARG_EDITOR_MODE to mode.name)
            }
        }
    }
}

enum class MealEditorMode {
    CREATE, EDIT
}