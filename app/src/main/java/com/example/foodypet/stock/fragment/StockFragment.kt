package com.example.foodypet.stock.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentStockBinding
import com.example.foodypet.stock.adapter.StockAdapter
import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.model.StockItem
import com.google.android.material.bottomsheet.BottomSheetDialog

class StockFragment : Fragment(R.layout.fragment_stock) {

    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!

    private lateinit var stockAdapter: StockAdapter
    private lateinit var expiredStockAdapter: StockAdapter

    private var selectedCategory: StockCategory = StockCategory.COOKED
    private var selectedSortType: StockSortType = StockSortType.EXPIRE

    private enum class StockSortType {
        NAME,
        CREATED,
        EXPIRE
    }

    private val stockItems = listOf(
        // COOKED - 일반 재고
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, false, "2025.03.01"),
        StockItem("26.08.15", "닭고기 완자", "2봉", StockCategory.COOKED, false, "2025.01.20"),
        StockItem("26.12.05", "소고기 미트볼", "1팩", StockCategory.COOKED, false, "2025.04.10"),
        StockItem("26.09.01", "오리 고기볼", "3봉", StockCategory.COOKED, false, "2025.02.12"),
        StockItem("26.07.20", "연어 큐브", "2팩", StockCategory.COOKED, false, "2025.05.03"),

        // COOKED - 유통기한 지난 음식
        StockItem("25.01.10", "고구마 치킨볼", "1봉", StockCategory.COOKED, true, "2024.11.01"),
        StockItem("24.12.25", "한우 야채죽", "1팩", StockCategory.COOKED, true, "2024.10.15"),
        StockItem("25.02.03", "단호박 미트볼", "2팩", StockCategory.COOKED, true, "2024.12.20"),

        // WET
        StockItem("26.12.01", "닭가슴살 습식캔", "2캔", StockCategory.WET, false, "2025.02.01"),
        StockItem("26.06.10", "참치 습식캔", "4캔", StockCategory.WET, false, "2025.01.11"),
        StockItem("25.03.05", "연어 습식파우치", "1개", StockCategory.WET, true, "2024.09.22"),

        // FRESH
        StockItem("26.05.12", "생닭 안심살", "1팩", StockCategory.FRESH, false, "2025.03.15"),
        StockItem("26.04.01", "생연어 슬라이스", "2팩", StockCategory.FRESH, false, "2025.02.18"),
        StockItem("25.02.14", "생오리 목뼈", "1팩", StockCategory.FRESH, true, "2024.08.30"),

        // DRY
        StockItem("26.10.01", "연어 건식 사료", "1봉", StockCategory.DRY, false, "2025.01.05"),
        StockItem("27.01.20", "양고기 건식 사료", "1봉", StockCategory.DRY, false, "2025.04.01"),
        StockItem("26.03.18", "오리 건식 사료", "2봉", StockCategory.DRY, false, "2025.02.25"),

        // SNACK
        StockItem("26.08.15", "강아지 간식", "3개", StockCategory.SNACK, false, "2025.03.08"),
        StockItem("26.02.10", "고구마 스틱", "5개", StockCategory.SNACK, false, "2025.01.25"),
        StockItem("25.01.01", "치킨 져키", "2개", StockCategory.SNACK, true, "2024.07.10")
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
        stockAdapter = StockAdapter { item ->
            // TODO: 일반 재고 아이템 클릭 처리
        }

        expiredStockAdapter = StockAdapter { item ->
            // TODO: 유통기한 지난 음식 아이템 클릭 처리
        }

        stockRecyclerView.apply {
            adapter = stockAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }

        expiredStockRecyclerView.apply {
            adapter = expiredStockAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }
    }

    private fun initClickListeners() = with(binding) {
        stockBackIv.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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
            // TODO: 음식 추가 화면으로 이동
            // findNavController().navigate(R.id.action_stockFragment_to_stockAddFragment)
        }
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
                if (isSelected) R.drawable.bg_stock_fill_5
                else R.drawable.bg_stock_unfill_5
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