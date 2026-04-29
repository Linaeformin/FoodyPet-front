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
import com.example.foodypet.stock.model.StockItem

class StockAssignFragment : Fragment() {

    private var _binding: FragmentStockAssignBinding? = null
    private val binding get() = _binding!!

    private lateinit var dropdownAdapter: StockDropdownAdapter
    private lateinit var stockAssignAdapter: StockAssignAdapter

    private val dummyStockItems = listOf(
        StockItem(
            expireDate = "2025-12-31",
            name = "[강아지 생식] 램포켓",
            count = "10",
            category = StockCategory.FRESH,
            isExpired = false,
            createdAt = "2025-01-01"
        ),
        StockItem(
            expireDate = "2025-12-31",
            name = "[강아지 생식] 램포켓",
            count = "8",
            category = StockCategory.FRESH,
            isExpired = false,
            createdAt = "2025-01-01"
        ),
        StockItem(
            expireDate = "2025-12-31",
            name = "[강아지 생식] 램포켓",
            count = "5",
            category = StockCategory.FRESH,
            isExpired = false,
            createdAt = "2025-01-01"
        ),
        StockItem(
            expireDate = "2025-12-31",
            name = "[강아지 생식] 램포켓",
            count = "3",
            category = StockCategory.FRESH,
            isExpired = false,
            createdAt = "2025-01-01"
        )
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

        showStockList(dummyStockItems)
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
                showStockList(dummyStockItems)
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
            showStockList(dummyStockItems)
            return
        }

        val searchResult = dummyStockItems.filter { stockItem ->
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