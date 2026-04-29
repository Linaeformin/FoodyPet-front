package com.example.foodypet.stock.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentStockAssignBinding
import com.example.foodypet.stock.adapter.StockAssignAdapter
import com.example.foodypet.stock.adapter.StockDropdownAdapter
import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.enum.StockDialogMode
import com.example.foodypet.stock.enum.StockSourceType
import com.example.foodypet.stock.model.StockItem

class StockAssignFragment : Fragment() {

    private var _binding: FragmentStockAssignBinding? = null
    private val binding get() = _binding!!

    private lateinit var dropdownAdapter: StockDropdownAdapter
    private lateinit var stockAssignAdapter: StockAssignAdapter

    private val stockItems = mutableListOf(
        // COOKED - 일반 재고
        StockItem("26.11.30", "흑돼지 치즈볼", "1봉", StockCategory.COOKED, false, "2025.03.01", StockSourceType.SERVICE),
        StockItem("26.08.15", "닭고기 완자", "2봉", StockCategory.COOKED, false, "2025.01.20", StockSourceType.SERVICE),
        StockItem("26.12.05", "소고기 미트볼", "1팩", StockCategory.COOKED, false, "2025.04.10", StockSourceType.USER),
        StockItem("26.09.01", "오리 고기볼", "3봉", StockCategory.COOKED, false, "2025.02.12", StockSourceType.SERVICE),
        StockItem("26.07.20", "연어 큐브", "2팩", StockCategory.COOKED, false, "2025.05.03", StockSourceType.USER),

        // COOKED - 유통기한 지난 음식
        StockItem("25.01.10", "고구마 치킨볼", "1봉", StockCategory.COOKED, true, "2024.11.01", StockSourceType.SERVICE),
        StockItem("24.12.25", "한우 야채죽", "1팩", StockCategory.COOKED, true, "2024.10.15", StockSourceType.USER),
        StockItem("25.02.03", "단호박 미트볼", "2팩", StockCategory.COOKED, true, "2024.12.20", StockSourceType.SERVICE),

        // WET
        StockItem("26.12.01", "닭가슴살 습식캔", "2캔", StockCategory.WET, false, "2025.02.01", StockSourceType.SERVICE),
        StockItem("26.06.10", "참치 습식캔", "4캔", StockCategory.WET, false, "2025.01.11", StockSourceType.SERVICE),
        StockItem("25.03.05", "연어 습식파우치", "1개", StockCategory.WET, true, "2024.09.22", StockSourceType.USER),

        // FRESH
        StockItem("26.05.12", "생닭 안심살", "1팩", StockCategory.FRESH, false, "2025.03.15", StockSourceType.USER),
        StockItem("26.04.01", "생연어 슬라이스", "2팩", StockCategory.FRESH, false, "2025.02.18", StockSourceType.SERVICE),
        StockItem("25.02.14", "생오리 목뼈", "1팩", StockCategory.FRESH, true, "2024.08.30", StockSourceType.USER),

        // DRY
        StockItem("26.10.01", "연어 건식 사료", "1봉", StockCategory.DRY, false, "2025.01.05", StockSourceType.SERVICE),
        StockItem("27.01.20", "양고기 건식 사료", "1봉", StockCategory.DRY, false, "2025.04.01", StockSourceType.SERVICE),
        StockItem("26.03.18", "오리 건식 사료", "2봉", StockCategory.DRY, false, "2025.02.25", StockSourceType.USER),

        // SNACK
        StockItem("26.08.15", "강아지 간식", "3개", StockCategory.SNACK, false, "2025.03.08", StockSourceType.SERVICE),
        StockItem("26.02.10", "고구마 스틱", "5개", StockCategory.SNACK, false, "2025.01.25", StockSourceType.USER),
        StockItem("25.01.01", "치킨 져키", "2개", StockCategory.SNACK, true, "2024.07.10", StockSourceType.SERVICE)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStockAssignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        initClickListener()
        initSearchListener()

        showStockList(stockItems)
    }

    private fun setupRecyclerView() = with(binding) {
        dropdownAdapter = StockDropdownAdapter { keyword ->
            stockSearchEt.setText(keyword)
            stockSearchEt.setSelection(keyword.length)

            hideDropdown()
            searchStock(keyword)
        }

        stockDropdownRv.apply {
            adapter = dropdownAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }

        stockAssignAdapter = StockAssignAdapter(
            onClickNutrition = { item ->
                showNutritionDialog()
            },
            onClickAssign = { item ->
                StockRegisterDialogFragment
                    .newInstance(StockDialogMode.EXIST)
                    .show(parentFragmentManager, "StockRegisterDialog")
            }
        )

        stockAssignRv.apply {
            adapter = stockAssignAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }
    }

    private fun initClickListener() {
        binding.stockBackIv.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.stockSearchIv.setOnClickListener {
            val keyword = binding.stockSearchEt.text.toString().trim()

            searchStock(keyword)
            hideDropdown()
            hideKeyboard()
        }

        binding.directRegisterBtn.setOnClickListener {
            StockRegisterDialogFragment
                .newInstance(StockDialogMode.NOT_EXIST)
                .show(parentFragmentManager, "StockRegisterDialog")
        }
    }

    private fun initSearchListener() {
        binding.stockSearchEt.addTextChangedListener { editable ->
            val keyword = editable.toString().trim()

            if (keyword.isBlank()) {
                hideDropdown()
                showStockList(stockItems)
                return@addTextChangedListener
            }

            getRecommendedKeywords(keyword)
        }

        binding.stockSearchEt.setOnEditorActionListener { _, _, _ ->
            val keyword = binding.stockSearchEt.text.toString().trim()

            searchStock(keyword)
            hideDropdown()
            hideKeyboard()

            true
        }
    }

    private fun getRecommendedKeywords(keyword: String) {
        val dummyKeywords = listOf(
            "${keyword}포",
            "${keyword}켓",
            "${keyword}프"
        ).take(3)

        dropdownAdapter.submitList(dummyKeywords)

        binding.stockDropdownRv.visibility =
            if (dummyKeywords.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun searchStock(keyword: String) {
        if (keyword.isBlank()) {
            showStockList(stockItems)
            return
        }

        val searchResult = stockItems.filter { stockItem ->
            stockItem.name.contains(keyword, ignoreCase = true)
        }

        if (searchResult.isEmpty()) {
            showEmptyState()
        } else {
            showStockList(searchResult)
        }
    }

    private fun hideAllSearchResult() = with(binding) {
        stockAssignAdapter.submitList(emptyList())

        stockAssignRv.visibility = View.GONE
        emptyStateContainer.visibility = View.GONE
        stockDropdownRv.visibility = View.GONE
    }

    private fun showEmptyState() = with(binding) {
        stockAssignAdapter.submitList(emptyList())

        stockAssignRv.visibility = View.GONE
        emptyStateContainer.visibility = View.VISIBLE
        stockDropdownRv.visibility = View.GONE
    }

    private fun showStockList(items: List<StockItem>) = with(binding) {
        emptyStateContainer.visibility = View.GONE
        stockDropdownRv.visibility = View.GONE
        stockAssignRv.visibility = View.VISIBLE

        stockAssignAdapter.submitList(items)
    }

    private fun hideDropdown() {
        binding.stockDropdownRv.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.stockSearchEt.windowToken, 0)
        binding.stockSearchEt.clearFocus()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}