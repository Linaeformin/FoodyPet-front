package com.example.foodypet.stock.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentStockAssignBinding
import com.example.foodypet.network.RetrofitClient
import com.example.foodypet.stock.adapter.StockAssignAdapter
import com.example.foodypet.stock.adapter.StockDropdownAdapter
import com.example.foodypet.stock.dto.FoodResponse
import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.enum.StockDialogMode
import com.example.foodypet.stock.enum.StockSourceType
import com.example.foodypet.stock.model.StockItem
import kotlinx.coroutines.launch

class StockAssignFragment : Fragment() {

    private var _binding: FragmentStockAssignBinding? = null
    private val binding get() = _binding!!

    private lateinit var dropdownAdapter: StockDropdownAdapter
    private lateinit var stockAssignAdapter: StockAssignAdapter

    private val stockItems = mutableListOf<StockItem>()

    companion object {
        private const val TAG = "StockAssignFragment"

        private const val API_BASE_URL = "http://15.135.188.209:8080"

        private const val S3_BASE_URL =
            "https://spring-upload-bucket-foodypet-561041808617-ap-southeast-2-an.s3.ap-southeast-2.amazonaws.com"
    }

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
        loadFoods()
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
                showNutritionDialog(item)
            },
            onClickAssign = { item ->
                StockRegisterDialogFragment
                    .newInstance(
                        mode = StockDialogMode.EXIST,
                        productName = item.name,
                        foodId = item.foodId
                    )
                    .show(parentFragmentManager, "StockRegisterDialog")
            }
        )

        stockAssignRv.apply {
            adapter = stockAssignAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }
    }

    private fun loadFoods() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getFoods()

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        stockItems.clear()

                        val foodItems = body.foods.map { food ->
                            food.toStockItem()
                        }

                        stockItems.addAll(foodItems)

                        if (stockItems.isEmpty()) {
                            showEmptyState()
                        } else {
                            showStockList(stockItems)
                        }
                    } else {
                        showEmptyState()
                    }
                } else {
                    Log.e(TAG, "시스템 재고 조회 실패 code: ${response.code()}")

                    Toast.makeText(
                        requireContext(),
                        "시스템 재고를 불러올 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    showEmptyState()
                }

            } catch (e: Exception) {
                Log.e(TAG, "시스템 재고 조회 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버와 연결할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()

                showEmptyState()
            }
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
        val recommendedKeywords = stockItems
            .map { it.name }
            .filter { name ->
                name.contains(keyword, ignoreCase = true)
            }
            .distinct()
            .take(3)

        dropdownAdapter.submitList(recommendedKeywords)

        binding.stockDropdownRv.visibility =
            if (recommendedKeywords.isEmpty()) View.GONE else View.VISIBLE
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

    private fun showNutritionDialog(item: StockItem) {
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_nutrition)
        dialog.setCanceledOnTouchOutside(true)

        val nutritionImageIv = dialog.findViewById<ImageView>(R.id.nutritionImageIv)

        if (nutritionImageIv == null) {
            Log.e(TAG, "nutrition_image_iv를 찾을 수 없습니다. dialog_nutrition.xml의 ImageView id를 확인해야 합니다.")
            dialog.dismiss()
            return
        }

        Log.d(TAG, "영양성분표 이미지 로드 시도: ${item.name}, ${item.nutritionImageUrl}")

        Glide.with(requireContext())
            .load(item.nutritionImageUrl)
            .placeholder(R.drawable.image_nutri)
            .error(R.drawable.image_nutri)
            .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {

                override fun onLoadFailed(
                    e: com.bumptech.glide.load.engine.GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e(
                        TAG,
                        "영양성분표 이미지 로드 실패: ${item.name}, url=${item.nutritionImageUrl}",
                        e
                    )
                    return false
                }

                override fun onResourceReady(
                    resource: android.graphics.drawable.Drawable,
                    model: Any,
                    target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(
                        TAG,
                        "영양성분표 이미지 로드 성공: ${item.name}, url=${item.nutritionImageUrl}"
                    )
                    return false
                }
            })
            .into(nutritionImageIv)

        dialog.show()

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.55f)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun FoodResponse.toStockItem(): StockItem {
        val fullImageUrl = imageUrl.toFullImageUrl()
        val fullNutritionImageUrl = nutritionImageUrl.toFullNutritionImageUrl()

        Log.d(TAG, "foodName: $foodName")
        Log.d(TAG, "imageUrl: $imageUrl")
        Log.d(TAG, "fullImageUrl: $fullImageUrl")
        Log.d(TAG, "nutritionImageUrl: $nutritionImageUrl")
        Log.d(TAG, "fullNutritionImageUrl: $fullNutritionImageUrl")

        return StockItem(
            foodId = foodId,
            name = foodName,
            count = unit.toDisplayUnit(),
            category = foodType.toStockCategory(),
            sourceType = foodSource.toStockSourceType(),
            imageUrl = fullImageUrl,
            nutritionImageUrl = fullNutritionImageUrl
        )
    }

    private fun String?.toFullNutritionImageUrl(): String? {
        if (this.isNullOrBlank()) return null

        val path = this.trim()

        return when {
            path.startsWith("http://") || path.startsWith("https://") -> {
                path
            }

            path.startsWith("/") -> {
                "$S3_BASE_URL$path"
            }

            else -> {
                "$S3_BASE_URL/$path"
            }
        }
    }

    private fun String?.toFullImageUrl(): String? {
        if (this.isNullOrBlank()) return null

        val path = this.trim()

        return when {
            path.startsWith("http://") || path.startsWith("https://") -> {
                path
            }

            path.startsWith("/images/") -> {
                val s3Path = path.removePrefix("/images/")
                "$S3_BASE_URL/$s3Path"
            }

            path.startsWith("images/") -> {
                val s3Path = path.removePrefix("images/")
                "$S3_BASE_URL/$s3Path"
            }

            path.startsWith("/") -> {
                "$API_BASE_URL$path"
            }

            else -> {
                "$API_BASE_URL/$path"
            }
        }
    }

    private fun String.toStockCategory(): StockCategory {
        return when (this.uppercase()) {
            "RAW" -> StockCategory.FRESH
            "COOKED" -> StockCategory.COOKED
            "WET" -> StockCategory.WET
            "DRY" -> StockCategory.DRY
            "SNACK" -> StockCategory.SNACK
            else -> StockCategory.COOKED
        }
    }

    private fun String.toStockSourceType(): StockSourceType {
        return when (this.uppercase()) {
            "SYSTEM" -> StockSourceType.SERVICE
            "USER" -> StockSourceType.USER
            else -> StockSourceType.SERVICE
        }
    }

    private fun String.toDisplayUnit(): String {
        return when (this.uppercase()) {
            "GRAM" -> "g"
            "COUNT" -> "개"
            "ML" -> "ml"
            else -> this
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}