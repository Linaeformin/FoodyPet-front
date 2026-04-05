package com.example.foodypet.home.fragment

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentDiaryBinding
import com.example.foodypet.home.enum.DiaryMode
import com.example.foodypet.home.model.MealItem

class DiaryFragment : Fragment(R.layout.fragment_diary) {

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private var selectedIntakeStatus: String = "다 먹음"
    private val selectedSymptoms = mutableSetOf<String>()
    private var selectedPreference: Int = 4

    private lateinit var mealPreviewIv: ImageView
    private lateinit var imagePlaceholderLayout: View

    private var selectedImageUri: Uri? = null
    private lateinit var diaryMode: DiaryMode

    companion object {
        private const val ARG_DIARY_MODE = "arg_diary_mode"

        fun newInstance(mode: DiaryMode): DiaryFragment {
            return DiaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DIARY_MODE, mode.name)
                }
            }
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                showSelectedImage(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDiaryBinding.bind(view)

        diaryMode = arguments?.getString(ARG_DIARY_MODE)
            ?.let { DiaryMode.valueOf(it) }
            ?: DiaryMode.REGISTER

        initTopBar()
        initBackButton()
        initIntakeStatus()
        initSymptoms()
        initPreference()
        initImageArea()
        initSupplementInputs()
        initActionButton()
        applyModeUi()
        openMealPop()
    }

    private fun initTopBar() {
        when (diaryMode) {
            DiaryMode.REGISTER -> {
                binding.mealTitleTv.text = "밥 일기 등록"
                binding.mealMoreIv.visibility = View.GONE
                binding.putFoodIv.visibility = View.VISIBLE
            }

            DiaryMode.EDIT -> {
                binding.mealTitleTv.text = "밥 일기 수정"
                binding.mealMoreIv.visibility = View.VISIBLE
                binding.putFoodIv.visibility = View.GONE

                binding.mealMoreIv.setOnClickListener {
                    showCustomMoreMenu()
                }
            }

            DiaryMode.READ -> {
                binding.mealTitleTv.text = "밥 일기"
                binding.mealMoreIv.visibility = View.VISIBLE
                binding.putFoodIv.visibility = View.GONE

                binding.mealMoreIv.setOnClickListener {
                    showCustomMoreMenu()
                }
            }
        }
    }

    private fun initBackButton() {
        binding.mealBackIv.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initIntakeStatus() {
        updateSingleSelect(
            selectedView = binding.tvEatAll,
            unselectedViews = listOf(binding.tvEatSome, binding.tvEatNone)
        )

        binding.tvEatAll.setOnClickListener {
            if (diaryMode == DiaryMode.READ) return@setOnClickListener

            selectedIntakeStatus = "다 먹음"
            updateSingleSelect(
                selectedView = binding.tvEatAll,
                unselectedViews = listOf(binding.tvEatSome, binding.tvEatNone)
            )
        }

        binding.tvEatSome.setOnClickListener {
            if (diaryMode == DiaryMode.READ) return@setOnClickListener

            selectedIntakeStatus = "조금 남김"
            updateSingleSelect(
                selectedView = binding.tvEatSome,
                unselectedViews = listOf(binding.tvEatAll, binding.tvEatNone)
            )
        }

        binding.tvEatNone.setOnClickListener {
            if (diaryMode == DiaryMode.READ) return@setOnClickListener

            selectedIntakeStatus = "안 먹음"
            updateSingleSelect(
                selectedView = binding.tvEatNone,
                unselectedViews = listOf(binding.tvEatAll, binding.tvEatSome)
            )
        }
    }

    private fun initSymptoms() {
        val symptomViews = listOf(
            binding.tvSymptomItch to "가려움",
            binding.tvSymptomDiarrhea to "설사",
            binding.tvSymptomTired to "무기력",
            binding.tvSymptomVomit to "구토"
        )

        symptomViews.forEach { (view, symptom) ->
            updateMultiSelectView(view, selectedSymptoms.contains(symptom))

            view.setOnClickListener {
                if (diaryMode == DiaryMode.READ) return@setOnClickListener

                if (selectedSymptoms.contains(symptom)) {
                    selectedSymptoms.remove(symptom)
                    updateMultiSelectView(view, false)
                } else {
                    selectedSymptoms.add(symptom)
                    updateMultiSelectView(view, true)
                }
            }
        }
    }

    private fun initPreference() {
        updatePreferenceIndicators(selectedPreference)

        val indicators = listOf(
            binding.preference1Iv,
            binding.preference2Iv,
            binding.preference3Iv,
            binding.preference4Iv,
            binding.preference5Iv
        )

        indicators.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                if (diaryMode == DiaryMode.READ) return@setOnClickListener

                selectedPreference = index + 1
                updatePreferenceIndicators(selectedPreference)
            }
        }
    }

    private fun initImageArea() {
        mealPreviewIv = binding.mealPreviewIv
        imagePlaceholderLayout = binding.imagePlaceholderLayout

        if (diaryMode == DiaryMode.READ) {
            binding.imageUploadArea.setOnClickListener(null)
            binding.mealImageAddIv.setOnClickListener(null)
        } else {
            binding.imageUploadArea.setOnClickListener {
                openGallery()
            }

            binding.mealImageAddIv.setOnClickListener {
                openGallery()
            }
        }

        binding.putFoodIv.setOnClickListener {
            if (diaryMode == DiaryMode.READ) return@setOnClickListener
            Toast.makeText(requireContext(), "음식 추가 클릭", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initSupplementInputs() {
        val testSupplements = listOf(
            SupplementUiModel(id = 1L, name = "유산균", amount = 1),
            SupplementUiModel(id = 2L, name = "오메가3", amount = 1),
            SupplementUiModel(id = 3L, name = "비타민D", amount = 0),
            SupplementUiModel(id = 4L, name = "루테인", amount = null)
        )

        renderSupplementInputs(testSupplements)
    }

    private fun renderSupplementInputs(items: List<SupplementUiModel>) {
        binding.supplementContainer.removeAllViews()

        items.forEachIndexed { index, item ->
            val itemView = createSupplementInputView(item)

            val params = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
                width = androidx.gridlayout.widget.GridLayout.LayoutParams.WRAP_CONTENT
                height = androidx.gridlayout.widget.GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = androidx.gridlayout.widget.GridLayout.spec(index % 2)
                rowSpec = androidx.gridlayout.widget.GridLayout.spec(index / 2)

                if (index % 2 == 0) {
                    rightMargin = dpToPx(32)
                }
                bottomMargin = dpToPx(12)
            }

            itemView.layoutParams = params
            binding.supplementContainer.addView(itemView)
        }
    }

    private fun createSupplementInputView(item: SupplementUiModel): View {
        val context = requireContext()

        val rowLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
        }

        val nameTextView = TextView(context).apply {
            text = item.name
            setTextColor(ContextCompat.getColor(context, R.color.black))
            textSize = 15f
            typeface = ResourcesCompat.getFont(context, R.font.scdream_light)
        }

        val amountEditText = EditText(context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(dpToPx(24), dpToPx(24)).apply {
                marginStart = dpToPx(6)
            }
            background = ContextCompat.getDrawable(context, R.drawable.bg_input_underline)
            gravity = Gravity.CENTER
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(2))
            setPadding(0, 0, 0, dpToPx(2))
            setTextColor(ContextCompat.getColor(context, R.color.black))
            textSize = 15f
            typeface = ResourcesCompat.getFont(context, R.font.scdream_light)
            tag = item.id
            isEnabled = diaryMode != DiaryMode.READ

            if (item.amount != null && item.amount != 0) {
                setText(item.amount.toString())
                setSelection(text.length)
            }
        }

        val unitTextView = TextView(context).apply {
            text = "정"
            setTextColor(ContextCompat.getColor(context, R.color.black))
            textSize = 15f
            typeface = ResourcesCompat.getFont(context, R.font.scdream_light)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = dpToPx(4)
            }
        }

        rowLayout.addView(nameTextView)
        rowLayout.addView(amountEditText)
        rowLayout.addView(unitTextView)

        return rowLayout
    }

    private fun initActionButton() {
        when (diaryMode) {
            DiaryMode.REGISTER -> {
                binding.mealRegisterBtn.visibility = View.VISIBLE
                binding.mealRegisterBtn.text = "등록하기"
            }

            DiaryMode.EDIT -> {
                binding.mealRegisterBtn.visibility = View.VISIBLE
                binding.mealRegisterBtn.text = "수정 완료"
            }

            DiaryMode.READ -> {
                binding.mealRegisterBtn.visibility = View.GONE
            }
        }

        binding.mealRegisterBtn.setOnClickListener {
            when (diaryMode) {
                DiaryMode.REGISTER -> registerDiary()
                DiaryMode.EDIT -> updateDiary()
                DiaryMode.READ -> Unit
            }
        }
    }

    private fun registerDiary() {
        Toast.makeText(requireContext(), "등록 처리", Toast.LENGTH_SHORT).show()
    }

    private fun updateDiary() {
        val dialog = MealActionDialogFragment(
            message = "밥 일기를 수정할까요?",
            actionText = "수정하기"
        ) {
            Toast.makeText(requireContext(), "수정 완료 처리", Toast.LENGTH_SHORT).show()

            parentFragmentManager.popBackStack()
        }

        dialog.show(parentFragmentManager, MealActionDialogFragment.TAG)
    }
    private fun applyModeUi() {
        val isReadMode = diaryMode == DiaryMode.READ

        binding.waterAmountEt.isEnabled = !isReadMode
        binding.memoEt.isEnabled = !isReadMode

        binding.tvEatAll.isEnabled = !isReadMode
        binding.tvEatSome.isEnabled = !isReadMode
        binding.tvEatNone.isEnabled = !isReadMode

        binding.tvSymptomItch.isEnabled = !isReadMode
        binding.tvSymptomDiarrhea.isEnabled = !isReadMode
        binding.tvSymptomTired.isEnabled = !isReadMode
        binding.tvSymptomVomit.isEnabled = !isReadMode

        binding.preference1Iv.isEnabled = !isReadMode
        binding.preference2Iv.isEnabled = !isReadMode
        binding.preference3Iv.isEnabled = !isReadMode
        binding.preference4Iv.isEnabled = !isReadMode
        binding.preference5Iv.isEnabled = !isReadMode
    }

    private fun updateSingleSelect(
        selectedView: View,
        unselectedViews: List<View>
    ) {
        setSelectedStyle(selectedView)
        unselectedViews.forEach { setUnselectedStyle(it) }
    }

    private fun updateMultiSelectView(view: View, isSelected: Boolean) {
        if (isSelected) {
            setSelectedStyle(view)
        } else {
            setUnselectedStyle(view)
        }
    }

    private fun setSelectedStyle(view: View) {
        if (view is TextView) {
            view.background = getDrawableCompat(R.drawable.bg_orange_fill_20)
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    private fun setUnselectedStyle(view: View) {
        if (view is TextView) {
            view.background = getDrawableCompat(R.drawable.bg_orange_stroke_20)
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_orange))
        }
    }

    private fun updatePreferenceIndicators(score: Int) {
        val indicators = listOf(
            binding.preference1Iv,
            binding.preference2Iv,
            binding.preference3Iv,
            binding.preference4Iv,
            binding.preference5Iv
        )

        indicators.forEachIndexed { index, imageView ->
            if (index < score) {
                imageView.setImageResource(R.drawable.icon_like)
            } else {
                imageView.setImageResource(R.drawable.icon_unlike)
            }
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun showSelectedImage(uri: Uri) {
        mealPreviewIv.visibility = View.VISIBLE
        imagePlaceholderLayout.visibility = View.GONE
        mealPreviewIv.setImageURI(uri)
    }

    private fun showCustomMoreMenu() {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.view_diary_more_menu, null)

        val popupWindow = PopupWindow(
            popupView,
            dpToPx(150),
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable())
            elevation = dpToPx(6).toFloat()
        }

        val editLayout = popupView.findViewById<View>(R.id.menu_edit_layout)
        val deleteLayout = popupView.findViewById<View>(R.id.menu_delete_layout)

        editLayout.visibility =
            if (diaryMode == DiaryMode.READ) View.VISIBLE else View.GONE

        deleteLayout.visibility =
            if (diaryMode == DiaryMode.READ || diaryMode == DiaryMode.EDIT) View.VISIBLE else View.GONE

        editLayout.setOnClickListener {
            popupWindow.dismiss()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DiaryFragment.newInstance(DiaryMode.EDIT))
                .addToBackStack(null)
                .commit()
        }

        deleteLayout.setOnClickListener {
            popupWindow.dismiss()

            val dialog = MealActionDialogFragment(
                message = "밥 일기를 삭제할까요?",
                actionText = "삭제하기"
            ) {
                deleteDiary()
            }

            dialog.show(parentFragmentManager, MealActionDialogFragment.TAG)
        }

        popupWindow.showAsDropDown(binding.mealMoreIv, -dpToPx(150), dpToPx(8))
    }

    private fun deleteDiary() {
        Toast.makeText(requireContext(), "삭제 완료", Toast.LENGTH_SHORT).show()

        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun getDrawableCompat(drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), drawableRes)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class SupplementUiModel(
        val id: Long,
        val name: String,
        val amount: Int? = null
    )

    data class SupplementInputResult(
        val id: Long,
        val name: String,
        val amount: Int
    )

    private fun openMealPop() {

        binding.putFoodIv.setOnClickListener {
            val mealList = listOf(
                MealItem(
                    mealId = 1L,
                    time = "08:30",
                    content = "삶은 닭가슴살 30g, 브로콜리 10g, 단호박 20g",
                    isFed = false
                ),
                MealItem(
                    mealId = 2L,
                    time = "13:00",
                    content = "흑돼지 치즈볼 1개, 닭오돌뼈 10g, 플라그오프, 뉴로액트",
                    isFed = false
                ),
                MealItem(
                    mealId = 3L,
                    time = "18:40",
                    content = "연어 25g, 고구마 15g, 유산균, 오메가3",
                    isFed = false
                )
            )

            val dialog = MealLoadDialogFragment(mealList) { selectedMeal ->
                binding.pageTitleChipTv.text = selectedMeal.time
                binding.mealFoodListTv.text = selectedMeal.content
            }

            dialog.show(parentFragmentManager, "MealLoadDialog")
        }
    }
}