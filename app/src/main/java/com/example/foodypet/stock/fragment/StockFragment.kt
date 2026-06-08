package com.example.foodypet.stock.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentStockBinding
import com.example.foodypet.network.RetrofitClient
import com.example.foodypet.stock.adapter.StockAdapter
import com.example.foodypet.stock.dto.FoodType
import com.example.foodypet.stock.dto.PetFoodStockSortType
import com.example.foodypet.stock.dto.StockItemResponse
import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.enum.StockDialogMode
import com.example.foodypet.stock.enum.StockScreenMode
import com.example.foodypet.stock.enum.StockSourceType
import com.example.foodypet.stock.model.StockItem
import com.example.foodypet.util.toStockQuantityWithUnitText
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

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

    private val stockItems = mutableListOf<StockItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentStockBinding.bind(view)

        setupRecyclerView()
        initClickListeners()
        updateCategoryUi()
        loadStocks()
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
                        toggleStockSelection(item)
                    }
                }
            },
            onCountChanged = { item, newCount ->
                updateStockCount(item, newCount)
            },
            onCheckClick = { item ->
                toggleStockSelection(item)
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
                        toggleStockSelection(item)
                    }
                }
            },
            onCountChanged = { item, newCount ->
                updateStockCount(item, newCount)
            },
            onCheckClick = { item ->
                toggleStockSelection(item)
            }
        )

        stockRecyclerView.adapter = stockAdapter
        expiredStockRecyclerView.adapter = expiredStockAdapter
    }

    private fun initClickListeners() = with(binding) {
        stockBackIv.setOnClickListener {
            when (currentScreenMode) {
                StockScreenMode.VIEW -> {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                StockScreenMode.EDIT -> {
                    exitEditMode()
                }

                StockScreenMode.DELETE -> {
                    exitDeleteMode()
                }
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
            enterDeleteMode()
        }

        stockEditCompleteBtn.setOnClickListener {
            when (currentScreenMode) {
                StockScreenMode.VIEW -> {
                    // 기본 모드에서는 버튼이 보이지 않음
                }

                StockScreenMode.EDIT -> {
                    // TODO: 서버에 수정된 재고 수량 반영 API 연결
                    exitEditMode()
                }

                StockScreenMode.DELETE -> {
                    deleteSelectedStocks()
                }
            }
        }
    }

    private fun loadStocks() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getStocks(
                    foodType = selectedCategory.toApiFoodType(),
                    treat = selectedCategory.isTreatCategory(),
                    sort = selectedSortType.toApiSortType()
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    val availableStocks = body?.availableStocks.orEmpty()
                    val expiredStocks = body?.expiredStocks.orEmpty()

                    Log.d(TAG, "availableStocks: $availableStocks")
                    Log.d(TAG, "expiredStocks: $expiredStocks")

                    stockItems.clear()
                    stockItems.addAll(
                        availableStocks.map { it.toStockItem(isExpired = false) }
                    )
                    stockItems.addAll(
                        expiredStocks.map { it.toStockItem(isExpired = true) }
                    )

                    renderStockList()

                } else {
                    Log.e(TAG, "error code: ${response.code()}")
                    Log.e(TAG, "error body: ${response.errorBody()?.string()}")

                    handleErrorResponse(response.code())
                }

            } catch (e: Exception) {
                Log.e(TAG, "loadStocks error", e)

                Toast.makeText(
                    requireContext(),
                    "서버와 연결할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleErrorResponse(code: Int) {
        val message = when (code) {
            400 -> "재고 조회 요청 형식이 올바르지 않습니다."
            401 -> "로그인이 필요합니다."
            403 -> "재고 조회 권한이 없습니다."
            404 -> "재고 정보를 찾을 수 없습니다."
            500 -> "서버 오류가 발생했습니다."
            else -> "재고 목록을 불러올 수 없습니다."
        }

        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun enterEditMode() = with(binding) {
        currentScreenMode = StockScreenMode.EDIT

        stockTitleTv.text = "재고 수정"

        stockAddBtn.visibility = View.GONE
        stockFabMenuLayout.visibility = View.GONE

        stockEditCompleteBtn.text = "수정 완료"
        stockEditCompleteBtn.visibility = View.VISIBLE

        setupAdapters(StockScreenMode.EDIT)
        renderStockList()
    }

    private fun exitEditMode() = with(binding) {
        currentScreenMode = StockScreenMode.VIEW

        stockTitleTv.text = "재고 관리"

        stockAddBtn.visibility = View.VISIBLE
        stockFabMenuLayout.visibility = View.GONE

        stockEditCompleteBtn.text = "수정 완료"
        stockEditCompleteBtn.visibility = View.GONE

        setupAdapters(StockScreenMode.VIEW)
        renderStockList()
    }

    private fun enterDeleteMode() = with(binding) {
        currentScreenMode = StockScreenMode.DELETE

        clearSelectedItems()

        stockTitleTv.text = "재고 삭제"

        stockAddBtn.visibility = View.GONE
        stockFabMenuLayout.visibility = View.GONE

        stockEditCompleteBtn.text = "삭제하기"
        stockEditCompleteBtn.visibility = View.VISIBLE

        setupAdapters(StockScreenMode.DELETE)
        renderStockList()
    }

    private fun exitDeleteMode() = with(binding) {
        currentScreenMode = StockScreenMode.VIEW

        clearSelectedItems()

        stockTitleTv.text = "재고 관리"

        stockAddBtn.visibility = View.VISIBLE
        stockFabMenuLayout.visibility = View.GONE

        stockEditCompleteBtn.text = "수정 완료"
        stockEditCompleteBtn.visibility = View.GONE

        setupAdapters(StockScreenMode.VIEW)
        renderStockList()
    }

    private fun toggleStockSelection(targetItem: StockItem) {
        val index = stockItems.indexOfFirst {
            isSameStockItem(it, targetItem)
        }

        if (index == -1) return

        stockItems[index] = stockItems[index].copy(
            isSelected = !stockItems[index].isSelected
        )

        renderStockList()
    }

    private fun deleteSelectedStocks() {
        val selectedItems = stockItems.filter { it.isSelected }

        if (selectedItems.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "삭제할 재고를 선택해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // TODO: 서버 삭제 API 연결
        // selectedItems를 서버에 보내서 삭제하면 됨

        stockItems.removeAll { it.isSelected }

        exitDeleteMode()
    }

    private fun clearSelectedItems() {
        for (i in stockItems.indices) {
            if (stockItems[i].isSelected) {
                stockItems[i] = stockItems[i].copy(isSelected = false)
            }
        }
    }

    private fun updateStockCount(targetItem: StockItem, newCount: String) {
        val index = stockItems.indexOfFirst {
            isSameStockItem(it, targetItem)
        }

        if (index == -1) return

        stockItems[index] = stockItems[index].copy(
            count = newCount
        )

        renderStockList()
    }

    private fun isSameStockItem(
        item: StockItem,
        targetItem: StockItem
    ): Boolean {
        return item.expireDate == targetItem.expireDate &&
                item.name == targetItem.name &&
                item.category == targetItem.category &&
                item.isExpired == targetItem.isExpired &&
                item.createdAt == targetItem.createdAt &&
                item.sourceType == targetItem.sourceType
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
        loadStocks()
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
        val sortedItems = when (selectedSortType) {
            StockSortType.NAME -> {
                stockItems.sortedBy { it.name }
            }

            StockSortType.CREATED -> {
                stockItems.sortedByDescending { it.createdAt }
            }

            StockSortType.EXPIRE -> {
                stockItems.sortedBy { it.expireDate }
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
            loadStocks()
            dialog.dismiss()
        }

        view.findViewById<TextView>(R.id.sort_created_tv).setOnClickListener {
            selectedSortType = StockSortType.CREATED
            binding.stockSortTv.text = "등록 순"
            loadStocks()
            dialog.dismiss()
        }

        view.findViewById<TextView>(R.id.sort_expiration_tv).setOnClickListener {
            selectedSortType = StockSortType.EXPIRE
            binding.stockSortTv.text = "유통기한 순"
            loadStocks()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun StockCategory.toApiFoodType(): FoodType {
        return when (this) {
            StockCategory.COOKED -> FoodType.COOKED
            StockCategory.WET -> FoodType.WET
            StockCategory.FRESH -> FoodType.RAW
            StockCategory.DRY -> FoodType.DRY
            StockCategory.SNACK -> FoodType.DRY
        }
    }

    private fun StockCategory.isTreatCategory(): Boolean {
        return this == StockCategory.SNACK
    }

    private fun StockSortType.toApiSortType(): PetFoodStockSortType {
        return when (this) {
            StockSortType.NAME -> PetFoodStockSortType.NAME
            StockSortType.CREATED -> PetFoodStockSortType.CREATED
            StockSortType.EXPIRE -> PetFoodStockSortType.EXPIRED
        }
    }

    private fun StockItemResponse.toStockItem(isExpired: Boolean): StockItem {
        return StockItem(
            expireDate = expiredAt.replace("-", "."),
            name = foodName,
            count = quantity.toStockQuantityWithUnitText(unit),
            category = foodType.toStockCategory(isTreat),
            isExpired = isExpired,
            createdAt = "",
            sourceType = source.name.toStockSourceType()
        )
    }

    private fun FoodType.toStockCategory(isTreat: Boolean): StockCategory {
        if (isTreat) return StockCategory.SNACK

        return when (this) {
            FoodType.COOKED -> StockCategory.COOKED
            FoodType.WET -> StockCategory.WET
            FoodType.RAW -> StockCategory.FRESH
            FoodType.DRY -> StockCategory.DRY
        }
    }

    private fun String.toStockSourceType(): StockSourceType {
        return when (this) {
            "SYSTEM" -> StockSourceType.SERVICE
            "CUSTOM" -> StockSourceType.USER
            else -> StockSourceType.SERVICE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "StockFragment"
    }
}