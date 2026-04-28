package com.example.foodypet.stock.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentStockBinding
import com.example.foodypet.stock.adapter.StockAdapter
import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.model.StockItem
import androidx.core.content.res.ResourcesCompat
class StockFragment : Fragment(R.layout.fragment_stock) {

    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!

    private lateinit var stockAdapter: StockAdapter
    private lateinit var expiredStockAdapter: StockAdapter

    private var selectedCategory: StockCategory = StockCategory.COOKED
    private var isExpireAsc: Boolean = true

    private val stockItems = listOf(
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, false),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, false),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, false),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, false),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, false),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, false),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, false),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, false),

        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, true),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, true),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, true),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, true),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, true),
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, true),

        StockItem("26.12.01", "닭가슴살 습식캔", "2캔", StockCategory.WET, false),
        StockItem("26.10.01", "연어 건식 사료", "1봉", StockCategory.DRY, false),
        StockItem("26.08.15", "강아지 간식", "3개", StockCategory.SNACK, false)
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
            isExpireAsc = !isExpireAsc
            renderStockList()
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
            .let { list ->
                if (isExpireAsc) {
                    list.sortedBy { it.expireDate }
                } else {
                    list.sortedByDescending { it.expireDate }
                }
            }

        val normalItems = filteredItems.filter { !it.isExpired }
        val expiredItems = filteredItems.filter { it.isExpired }

        stockAdapter.submitList(normalItems)
        expiredStockAdapter.submitList(expiredItems)

        // 일반 재고 영역
        if (normalItems.isEmpty()) {
            stockRecyclerView.visibility = View.GONE
            stockEmptyTv.visibility = View.VISIBLE
        } else {
            stockRecyclerView.visibility = View.VISIBLE
            stockEmptyTv.visibility = View.GONE
        }

        // 유통기한 지난 음식 영역
        expiredTitleTv.visibility = View.VISIBLE

        if (expiredItems.isEmpty()) {
            expiredStockRecyclerView.visibility = View.GONE
            expiredEmptyTv.visibility = View.VISIBLE
        } else {
            expiredStockRecyclerView.visibility = View.VISIBLE
            expiredEmptyTv.visibility = View.GONE
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}