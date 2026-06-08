package com.example.foodypet.stock.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.foodypet.R
import com.example.foodypet.databinding.DialogStockRegisterBinding
import com.example.foodypet.network.RetrofitClient
import com.example.foodypet.stock.dto.AssignFoodStockRequest
import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.enum.StockDialogMode
import kotlinx.coroutines.launch

class StockRegisterDialogFragment : DialogFragment() {

    private var _binding: DialogStockRegisterBinding? = null
    private val binding get() = _binding!!

    private var isSnackChecked = false
    private var selectedCategory = StockCategory.COOKED
    private var selectedNutritionImageUri: Uri? = null

    private val pickNutritionImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedNutritionImageUri = uri
                showNutritionPreview(uri)
            }
        }

    private val dialogMode: StockDialogMode by lazy {
        arguments?.getString(ARG_MODE)?.let { modeName ->
            StockDialogMode.valueOf(modeName)
        } ?: StockDialogMode.EXIST
    }

    private val dialogTitle: String by lazy {
        arguments?.getString(ARG_TITLE) ?: "재고 등록"
    }

    private val selectedProductName: String by lazy {
        arguments?.getString(ARG_PRODUCT_NAME).orEmpty()
    }

    private val selectedFoodId: Long? by lazy {
        if (arguments?.containsKey(ARG_FOOD_ID) == true) {
            arguments?.getLong(ARG_FOOD_ID)
        } else {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogStockRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val dialogWidth = (resources.displayMetrics.widthPixels * 0.86).toInt()

            setLayout(
                dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogTitleTv.text = dialogTitle

        applySelectedProduct()
        applyDialogMode()
        initUnitDropdown()
        initCategoryClickListeners()
        initClickListeners()
    }

    private fun applySelectedProduct() {
        if (selectedProductName.isNotBlank()) {
            binding.productNameTv.text = selectedProductName
        }
    }

    private fun applyDialogMode() {
        when (dialogMode) {
            StockDialogMode.EXIST -> {
                binding.stockCategoryLayout.visibility = View.GONE
                binding.nutritionContainer.visibility = View.GONE

                isSnackChecked = false
                updateSnackCheckImage()
            }

            StockDialogMode.NOT_EXIST -> {
                binding.stockCategoryLayout.visibility = View.VISIBLE
                binding.nutritionContainer.visibility = View.VISIBLE

                selectCategory(StockCategory.COOKED)

                isSnackChecked = false
                updateSnackCheckImage()
            }
        }
    }

    private fun initUnitDropdown() {
        val units = listOf("g", "ml", "개", "봉")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_unit,
            units
        )

        binding.unitDropdownActv.setAdapter(adapter)

        binding.unitDropdownActv.setOnClickListener {
            binding.unitDropdownActv.showDropDown()
        }

        binding.unitDropdownActv.setOnItemClickListener { _, _, position, _ ->
            binding.unitDropdownActv.setText(units[position], false)
        }

        binding.unitDropdownActv.setText("g", false)
    }

    private fun initCategoryClickListeners() {
        binding.categoryCookedTv.setOnClickListener {
            selectCategory(StockCategory.COOKED)
        }

        binding.categoryWetTv.setOnClickListener {
            selectCategory(StockCategory.WET)
        }

        binding.categoryFreshTv.setOnClickListener {
            selectCategory(StockCategory.FRESH)
        }

        binding.categoryDryTv.setOnClickListener {
            selectCategory(StockCategory.DRY)
        }
    }

    private fun initClickListeners() {
        binding.closeIv.setOnClickListener {
            dismiss()
        }

        binding.snackCheckIv.setOnClickListener {
            toggleSnackCheck()
        }

        binding.snackTv.setOnClickListener {
            toggleSnackCheck()
        }

        binding.nutritionFileIv.setOnClickListener {
            openNutritionImagePicker()
        }

        binding.stockAssignBtn.setOnClickListener {
            when (dialogMode) {
                StockDialogMode.EXIST -> {
                    assignExistingFoodStock()
                }

                StockDialogMode.NOT_EXIST -> {
                    // TODO: 신규 상품 직접 등록 API는 별도 명세 받으면 연결
                    Toast.makeText(
                        requireContext(),
                        "직접 등록 API는 아직 연결되지 않았습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun assignExistingFoodStock() {
        val foodId = selectedFoodId

        if (foodId == null) {
            Toast.makeText(
                requireContext(),
                "상품 정보를 찾을 수 없습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val expiredDate = binding.expiredDateEt.text.toString().trim()
        val quantityText = binding.quantityEt.text.toString().trim()
        val unitText = binding.unitDropdownActv.text.toString().trim()

        if (expiredDate.isBlank()) {
            Toast.makeText(
                requireContext(),
                "유통기한을 입력해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (quantityText.isBlank()) {
            Toast.makeText(
                requireContext(),
                "총 수량을 입력해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val quantity = quantityText.toIntOrNull()

        if (quantity == null || quantity <= 0) {
            Toast.makeText(
                requireContext(),
                "수량은 1 이상으로 입력해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val request = AssignFoodStockRequest(
            foodId = foodId,
            expiredAt = expiredDate.toServerDate(),
            isTreat = isSnackChecked,
            unit = unitText.toServerUnit(),
            quantity = quantity
        )

        binding.stockAssignBtn.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.assignFoodStock(request)

                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "재고 등록이 완료되었습니다."

                    Toast.makeText(
                        requireContext(),
                        message,
                        Toast.LENGTH_SHORT
                    ).show()

                    dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "재고를 등록할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.stockAssignBtn.isEnabled = true
                }

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "서버와 연결할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()

                binding.stockAssignBtn.isEnabled = true
            }
        }
    }

    private fun String.toServerDate(): String {
        return this.trim().replace(".", "-")
    }

    private fun String.toServerUnit(): String {
        return when (this) {
            "g" -> "GRAM"
            "ml" -> "ML"
            "개" -> "COUNT"
            "봉" -> "BAG"
            else -> "GRAM"
        }
    }

    private fun openNutritionImagePicker() {
        pickNutritionImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun showNutritionPreview(uri: Uri) {
        binding.nutritionFileIv.apply {
            setImageURI(uri)
            setBackgroundResource(R.drawable.bg_nutrition_image_border)
            scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE

            val previewPadding = dpToPx(3)
            setPadding(
                previewPadding,
                previewPadding,
                previewPadding,
                previewPadding
            )
        }
    }

    private fun selectCategory(category: StockCategory) {
        selectedCategory = category

        setCategorySelected(binding.categoryCookedTv, category == StockCategory.COOKED)
        setCategorySelected(binding.categoryWetTv, category == StockCategory.WET)
        setCategorySelected(binding.categoryFreshTv, category == StockCategory.FRESH)
        setCategorySelected(binding.categoryDryTv, category == StockCategory.DRY)
    }

    private fun setCategorySelected(textView: TextView, isSelected: Boolean) {
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
                Color.BLACK
            }
        )

        val fontRes = if (isSelected) {
            R.font.scdream_medium
        } else {
            R.font.scdream_light
        }

        textView.typeface =
            ResourcesCompat.getFont(requireContext(), fontRes) ?: Typeface.DEFAULT
    }

    private fun toggleSnackCheck() {
        isSnackChecked = !isSnackChecked
        updateSnackCheckImage()
    }

    private fun updateSnackCheckImage() {
        binding.snackCheckIv.setImageResource(
            if (isSnackChecked) {
                R.drawable.icon_square_check
            } else {
                R.drawable.icon_square_uncheck
            }
        )
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODE = "mode"
        private const val ARG_TITLE = "title"
        private const val ARG_PRODUCT_NAME = "product_name"
        private const val ARG_FOOD_ID = "food_id"

        fun newInstance(
            mode: StockDialogMode,
            title: String = "재고 등록",
            productName: String? = null,
            foodId: Long? = null
        ): StockRegisterDialogFragment {
            return StockRegisterDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, mode.name)
                    putString(ARG_TITLE, title)

                    if (!productName.isNullOrBlank()) {
                        putString(ARG_PRODUCT_NAME, productName)
                    }

                    if (foodId != null) {
                        putLong(ARG_FOOD_ID, foodId)
                    }
                }
            }
        }
    }
}