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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.example.foodypet.R
import com.example.foodypet.databinding.DialogStockRegisterBinding
import com.example.foodypet.stock.enum.StockCategory
import com.example.foodypet.stock.enum.StockDialogMode

class StockRegisterDialogFragment : DialogFragment() {

    private var _binding: DialogStockRegisterBinding? = null
    private val binding get() = _binding!!

    private var isSnackChecked = true
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

        applyDialogMode()
        initUnitDropdown()
        initCategoryClickListeners()
        initClickListeners()
    }

    private fun applyDialogMode() {
        when (dialogMode) {
            StockDialogMode.EXIST -> {
                binding.stockCategoryLayout.visibility = View.GONE
                binding.nutritionContainer.visibility = View.GONE

                isSnackChecked = true
                updateSnackCheckImage()
            }

            StockDialogMode.NOT_EXIST -> {
                binding.stockCategoryLayout.visibility = View.VISIBLE
                binding.nutritionContainer.visibility = View.VISIBLE

                selectCategory(StockCategory.COOKED)

                isSnackChecked = true
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
            val productName = binding.productNameTv.text.toString()
            val expiredDate = binding.expiredDateEt.text.toString()
            val quantity = binding.quantityEt.text.toString()
            val unit = binding.unitDropdownActv.text.toString()

            when (dialogMode) {
                StockDialogMode.EXIST -> {
                    // TODO: 기존 상품 재고 등록
                    // viewModel.registerExistStock(
                    //     productName = productName,
                    //     expiredDate = expiredDate,
                    //     quantity = quantity.toInt(),
                    //     unit = unit,
                    //     isSnack = isSnackChecked
                    // )
                }

                StockDialogMode.NOT_EXIST -> {
                    // TODO: 신규 상품 재고 등록
                    // selectedNutritionImageUri를 서버에 multipart로 넘기면 됨
                    // viewModel.registerNotExistStock(
                    //     productName = productName,
                    //     category = selectedCategory,
                    //     expiredDate = expiredDate,
                    //     quantity = quantity.toInt(),
                    //     unit = unit,
                    //     isSnack = isSnackChecked,
                    //     nutritionImageUri = selectedNutritionImageUri
                    // )
                }
            }

            dismiss()
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

        textView.typeface = ResourcesCompat.getFont(requireContext(), fontRes) ?: Typeface.DEFAULT
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

        fun newInstance(mode: StockDialogMode): StockRegisterDialogFragment {
            return StockRegisterDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, mode.name)
                }
            }
        }
    }
}