package com.example.foodypet.stock.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentStockBinding
import com.example.foodypet.stock.adapter.StockAdapter
import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.enum.StockDialogMode
import com.example.foodypet.stock.enum.StockScreenMode
import com.example.foodypet.stock.enum.StockSourceType
import com.example.foodypet.stock.model.StockItem
import com.google.android.material.bottomsheet.BottomSheetDialog

class StockFragment : Fragment(R.layout.fragment_stock) {

    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!

    private lateinit var stockAdapter: StockAdapter
    private lateinit var expiredStockAdapter: StockAdapter

    private var selectedCategory: StockCategory = StockCategory.COOKED
    private var selectedSortType: StockSortType = StockSortType.EXPIRE
    private var currentScreenMode: StockScreenMode = StockScreenMode.VIEW

    private var isFabMenuOpen = false

    private enum class StockSortType {
        NAME,
        CREATED,
        EXPIRE
    }

    private val stockItems = mutableListOf(
        // COOKED - 일반 재고
        StockItem(
            "26.11.30",
            "흑돼지 치즈볼",
            "1봉",
            StockCategory.COOKED,
            false,
            "2025.03.01",
            StockSourceType.SERVICE
        ),
        StockItem(
            "26.08.15",
            "닭고기 완자",
            "2봉",
            StockCategory.COOKED,
            false,
            "2025.01.20",
            StockSourceType.SERVICE
        ),
        StockItem(
            "26.12.05",
            "소고기 미트볼",
            "1팩",
            StockCategory.COOKED,
            false,
            "2025.04.10",
            StockSourceType.USER
        ),
        StockItem(
            "26.09.01",
            "오리 고기볼",
            "3봉",
            StockCategory.COOKED,
            false,
            "2025.02.12",
            StockSourceType.SERVICE
        ),
        StockItem(
            "26.07.20",
            "연어 큐브",
            "2팩",
            StockCategory.COOKED,
            false,
            "2025.05.03",
            StockSourceType.USER
        ),

        // COOKED - 유통기한 지난 음식
        StockItem(
            "25.01.10",
            "고구마 치킨볼",
            "1봉",
            StockCategory.COOKED,
            true,
            "2024.11.01",
            StockSourceType.SERVICE
        ),
        StockItem(
            "24.12.25",
            "한우 야채죽",
            "1팩",
            StockCategory.COOKED,
            true,
            "2024.10.15",
            StockSourceType.USER
        ),
        StockItem(
            "25.02.03",
            "단호박 미트볼",
            "2팩",
            StockCategory.COOKED,
            true,
            "2024.12.20",
            StockSourceType.SERVICE
        ),

        // WET
        StockItem(
            "26.12.01",
            "닭가슴살 습식캔",
            "2캔",
            StockCategory.WET,
            false,
            "2025.02.01",
            StockSourceType.SERVICE
        ),
        StockItem(
            "26.06.10",
            "참치 습식캔",
            "4캔",
            StockCategory.WET,
            false,
            "2025.01.11",
            StockSourceType.SERVICE
        ),
        StockItem(
            "25.03.05",
            "연어 습식파우치",
            "1개",
            StockCategory.WET,
            true,
            "2024.09.22",
            StockSourceType.USER
        ),

        // FRESH
        StockItem(
            "26.05.12",
            "생닭 안심살",
            "1팩",
            StockCategory.FRESH,
            false,
            "2025.03.15",
            StockSourceType.USER
        ),
        StockItem(
            "26.04.01",
            "생연어 슬라이스",
            "2팩",
            StockCategory.FRESH,
            false,
            "2025.02.18",
            StockSourceType.SERVICE
        ),
        StockItem(
            "25.02.14",
            "생오리 목뼈",
            "1팩",
            StockCategory.FRESH,
            true,
            "2024.08.30",
            StockSourceType.USER
        ),

        // DRY
        StockItem(
            "26.10.01",
            "연어 건식 사료",
            "1봉",
            StockCategory.DRY,
            false,
            "2025.01.05",
            StockSourceType.SERVICE
        ),
        StockItem(
            "27.01.20",
            "양고기 건식 사료",
            "1봉",
            StockCategory.DRY,
            false,
            "2025.04.01",
            StockSourceType.SERVICE
        ),
        StockItem(
            "26.03.18",
            "오리 건식 사료",
            "2봉",
            StockCategory.DRY,
            false,
            "2025.02.25",
            StockSourceType.USER
        ),

        // SNACK
        StockItem(
            "26.08.15",
            "강아지 간식",
            "3개",
            StockCategory.SNACK,
            false,
            "2025.03.08",
            StockSourceType.SERVICE
        ),
        StockItem(
            "26.02.10",
            "고구마 스틱",
            "5개",
            StockCategory.SNACK,
            false,
            "2025.01.25",
            StockSourceType.USER
        ),
        StockItem(
            "25.01.01",
            "치킨 져키",
            "2개",
            StockCategory.SNACK,
            true,
            "2024.07.10",
            StockSourceType.SERVICE
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentStockBinding.bind(view)

        setupRecyclerView()
        initClickListeners()
        updateCategoryUi()
        renderStockList()
    }

    private fun setupRecyclerView() = with(binding) {
        setupAdapters(StockScreenMode.VIEW)

        stockRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }

        expiredStockRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }
    }

    private fun setupAdapters(screenMode: StockScreenMode) = with(binding) {
        currentScreenMode = screenMode

        stockAdapter = StockAdapter(
            screenMode = currentScreenMode,
            onItemClick = { item ->
                when (currentScreenMode) {
                    StockScreenMode.VIEW -> {
                        showNutritionDialog()
                    }

                    StockScreenMode.EDIT -> {
                        showStockRegisterDialog(item)
                    }

                    StockScreenMode.DELETE -> {

                    }
                }
            },
            onCountChanged = { item, newCount ->
                updateStockCount(item, newCount)
            }
        )

        expiredStockAdapter = StockAdapter(
            screenMode = currentScreenMode,
            onItemClick = { item ->
                when (currentScreenMode) {
                    StockScreenMode.VIEW -> {
                        showNutritionDialog()
                    }

                    StockScreenMode.EDIT -> {
                        showStockRegisterDialog(item)
                    }

                    StockScreenMode.DELETE -> {

                    }
                }
            },
            onCountChanged = { item, newCount ->
                updateStockCount(item, newCount)
            }
        )

        stockRecyclerView.adapter = stockAdapter
        expiredStockRecyclerView.adapter = expiredStockAdapter
    }
    private fun initClickListeners() = with(binding) {
        stockBackIv.setOnClickListener {
            if (currentScreenMode == StockScreenMode.EDIT) {
                exitEditMode()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        categoryCookedTv.setOnClickListener {
            changeCategory(StockCategory.COOKED)
        }

        categoryWetTv.setOnClickListener {
            changeCategory(StockCategory.WET)
        }

        categoryFreshTv.setOnClickListener {
            changeCategory(StockCategory.FRESH)
        }

        categoryDryTv.setOnClickListener {
            changeCategory(StockCategory.DRY)
        }

        categorySnackTv.setOnClickListener {
            changeCategory(StockCategory.SNACK)
        }

        stockSortLayout.setOnClickListener {
            showSortDialog()
        }

        stockAddBtn.setOnClickListener {
            toggleFabMenu()
        }

        stockMenuAddTv.setOnClickListener {
            closeFabMenu()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StockAssignFragment())
                .addToBackStack(null)
                .commit()
        }

        stockMenuEditTv.setOnClickListener {
            closeFabMenu()
            enterEditMode()
        }

        stockMenuDeleteTv.setOnClickListener {
            closeFabMenu()

            // TODO: 삭제 모드 진입 또는 삭제 처리
        }
    }

    private fun enterEditMode() = with(binding) {
        currentScreenMode = StockScreenMode.EDIT

        stockTitleTv.text = "재고 수정"

        setupAdapters(StockScreenMode.EDIT)
        renderStockList()
    }

    private fun exitEditMode() = with(binding) {
        currentScreenMode = StockScreenMode.VIEW

        stockTitleTv.text = "재고 관리"

        setupAdapters(StockScreenMode.VIEW)
        renderStockList()
    }

    private fun updateStockCount(targetItem: StockItem, newCount: String) {
        val index = stockItems.indexOfFirst {
            it.expireDate == targetItem.expireDate &&
                    it.name == targetItem.name &&
                    it.category == targetItem.category &&
                    it.isExpired == targetItem.isExpired &&
                    it.createdAt == targetItem.createdAt &&
                    it.sourceType == targetItem.sourceType
        }

        if (index == -1) return

        stockItems[index] = stockItems[index].copy(
            count = newCount
        )

        renderStockList()
    }

    private fun showStockRegisterDialog(item: StockItem) {
        val dialogMode = when (item.sourceType) {
            StockSourceType.USER -> StockDialogMode.NOT_EXIST
            StockSourceType.SERVICE -> StockDialogMode.EXIST
        }

        val dialogTitle = when (currentScreenMode) {
            StockScreenMode.VIEW -> "재고 등록"
            StockScreenMode.EDIT -> "재고 수정"
            StockScreenMode.DELETE -> "재고 삭제"
        }

        StockRegisterDialogFragment
            .newInstance(
                mode = dialogMode,
                title = dialogTitle
            )
            .show(parentFragmentManager, "StockRegisterDialog")
    }

    private fun showNutritionDialog() {
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_nutrition)
        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.55f)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        dialog.show()

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun toggleFabMenu() = with(binding) {
        isFabMenuOpen = !isFabMenuOpen

        stockFabMenuLayout.visibility = if (isFabMenuOpen) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun closeFabMenu() = with(binding) {
        isFabMenuOpen = false
        stockFabMenuLayout.visibility = View.GONE
    }

    private fun changeCategory(category: StockCategory) {
        selectedCategory = category
        updateCategoryUi()
        renderStockList()
    }

    private fun updateCategoryUi() = with(binding) {
        val mediumFont = ResourcesCompat.getFont(requireContext(), R.font.scdream_medium)
        val lightFont = ResourcesCompat.getFont(requireContext(), R.font.scdream_light)

        val categoryViews = mapOf(
            StockCategory.COOKED to categoryCookedTv,
            StockCategory.WET to categoryWetTv,
            StockCategory.FRESH to categoryFreshTv,
            StockCategory.DRY to categoryDryTv,
            StockCategory.SNACK to categorySnackTv
        )

        categoryViews.forEach { (category, textView) ->
            val isSelected = category == selectedCategory

            textView.setBackgroundResource(
                if (isSelected) {
                    R.drawable.bg_stock_fill_5
                } else {
                    R.drawable.bg_stock_unfill_5
                }
            )

            textView.setTextColor(
                if (isSelected) {
                    Color.WHITE
                } else {
                    ContextCompat.getColor(requireContext(), R.color.black)
                }
            )

            textView.typeface = if (isSelected) {
                mediumFont
            } else {
                lightFont
            }
        }
    }

    private fun renderStockList() = with(binding) {
        val filteredItems = stockItems
            .filter { it.category == selectedCategory }

        val sortedItems = when (selectedSortType) {
            StockSortType.NAME -> {
                filteredItems.sortedBy { it.name }
            }

            StockSortType.CREATED -> {
                filteredItems.sortedByDescending { it.createdAt }
            }

            StockSortType.EXPIRE -> {
                filteredItems.sortedBy { it.expireDate }
            }
        }

        val normalItems = sortedItems.filter { !it.isExpired }
        val expiredItems = sortedItems.filter { it.isExpired }

        stockAdapter.submitList(normalItems)
        expiredStockAdapter.submitList(expiredItems)

        if (normalItems.isEmpty()) {
            stockRecyclerView.visibility = View.GONE
            stockEmptyTv.visibility = View.VISIBLE
        } else {
            stockRecyclerView.visibility = View.VISIBLE
            stockEmptyTv.visibility = View.GONE
        }

        expiredTitleTv.visibility = View.VISIBLE

        if (expiredItems.isEmpty()) {
            expiredStockRecyclerView.visibility = View.GONE
            expiredEmptyTv.visibility = View.VISIBLE
        } else {
            expiredStockRecyclerView.visibility = View.VISIBLE
            expiredEmptyTv.visibility = View.GONE
        }
    }

    private fun showSortDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_stock_sort, null)

        view.findViewById<TextView>(R.id.sort_name_tv).setOnClickListener {
            selectedSortType = StockSortType.NAME
            binding.stockSortTv.text = "가나다 순"
            renderStockList()
            dialog.dismiss()
        }

        view.findViewById<TextView>(R.id.sort_created_tv).setOnClickListener {
            selectedSortType = StockSortType.CREATED
            binding.stockSortTv.text = "등록 순"
            renderStockList()
            dialog.dismiss()
        }

        view.findViewById<TextView>(R.id.sort_expiration_tv).setOnClickListener {
            selectedSortType = StockSortType.EXPIRE
            binding.stockSortTv.text = "유통기한 순"
            renderStockList()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}