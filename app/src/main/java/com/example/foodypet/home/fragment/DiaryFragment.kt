package com.example.foodypet.home.fragment

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputFilter
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentDiaryBinding
import com.example.foodypet.home.dto.MealDiaryCapsuleRequest
import com.example.foodypet.home.dto.MealDiaryCreateRequest
import com.example.foodypet.home.dto.MealDiaryDetailCapsuleResponse
import com.example.foodypet.home.dto.MealDiaryDetailResponse
import com.example.foodypet.home.enum.DiaryMode
import com.example.foodypet.home.model.MealItem
import com.example.foodypet.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryFragment : Fragment(R.layout.fragment_diary) {

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealPreviewIv: ImageView
    private lateinit var imagePlaceholderLayout: View

    private var selectedImageUri: Uri? = null
    private lateinit var diaryMode: DiaryMode

    private var petId: Long = -1L
    private var mealDiaryId: Long = -1L

    private var petName: String? = null
    private var mealTime: String? = null
    private var mealContent: String? = null

    private var dailyDietId: Long = -1L
    private var petMealScheduleId: Long = -1L
    private var mealListCache: List<MealItem> = emptyList()

    private var selectedIntakeStatus: String? = null
    private val selectedSymptoms = mutableSetOf<String>()
    private var selectedPreference: Int = 0

    companion object {
        private const val ARG_MODE = "arg_mode"
        private const val ARG_PET_ID = "arg_pet_id"
        private const val ARG_MEAL_DIARY_ID = "arg_meal_diary_id"
        private const val ARG_PET_NAME = "arg_pet_name"
        private const val ARG_MEAL_TIME = "arg_meal_time"
        private const val ARG_MEAL_CONTENT = "arg_meal_content"

        fun newInstance(
            mode: DiaryMode,
            petId: Long,
            mealDiaryId: Long = -1L,
            petName: String? = null,
            mealTime: String? = null,
            mealContent: String? = null
        ): DiaryFragment {
            return DiaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, mode.name)
                    putLong(ARG_PET_ID, petId)
                    putLong(ARG_MEAL_DIARY_ID, mealDiaryId)
                    putString(ARG_PET_NAME, petName)
                    putString(ARG_MEAL_TIME, mealTime)
                    putString(ARG_MEAL_CONTENT, mealContent)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        diaryMode = arguments?.getString(ARG_MODE)
            ?.let { DiaryMode.valueOf(it) }
            ?: DiaryMode.REGISTER

        petId = arguments?.getLong(ARG_PET_ID, -1L) ?: -1L
        mealDiaryId = arguments?.getLong(ARG_MEAL_DIARY_ID, -1L) ?: -1L

        petName = arguments?.getString(ARG_PET_NAME)
        mealTime = arguments?.getString(ARG_MEAL_TIME)
        mealContent = arguments?.getString(ARG_MEAL_CONTENT)
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

        initTopBar()
        initBackButton()
        initMealInfo()
        initIntakeStatus()
        initSymptoms()
        initPreference()
        initImageArea()
        initActionButton()
        applyModeUi()
        initMealLoadButton()

        when (diaryMode) {
            DiaryMode.REGISTER -> {
                initSupplementInputs()
                loadMealWriteForm(showDialog = false)
            }

            DiaryMode.EDIT,
            DiaryMode.READ -> {
                loadMealDiaryDetail()
            }
        }
    }

    private fun loadMealDiaryDetail() {
        if (mealDiaryId == -1L) {
            Toast.makeText(
                requireContext(),
                "밥 일기 정보를 확인할 수 없습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMealDiaryDetail(mealDiaryId)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body == null) {
                        Toast.makeText(
                            requireContext(),
                            "밥 일기 정보를 불러올 수 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    bindMealDiaryDetail(body)
                } else {
                    Log.e(
                        "DiaryFragment",
                        "밥 일기 상세 조회 실패 code=${response.code()}, error=${response.errorBody()?.string()}"
                    )

                    Toast.makeText(
                        requireContext(),
                        "밥 일기 정보를 불러올 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("DiaryFragment", "밥 일기 상세 조회 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버 연결 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun bindMealDiaryDetail(detail: MealDiaryDetailResponse) {
        mealDiaryId = detail.diaryId
        petId = detail.petId
        dailyDietId = detail.dailyDietId
        petMealScheduleId = detail.petMealScheduleId

        binding.mealTitleTv.text = when (diaryMode) {
            DiaryMode.READ -> "밥 일기"
            DiaryMode.EDIT -> "밥 일기 수정"
            DiaryMode.REGISTER -> "밥 일기 등록"
        }

        binding.mealDateTv.text = formatApiDateToDisplayDate(detail.diaryDate)

        binding.pageTitleChipTv.text = mealTime.orEmpty()

        binding.mealFoodListTv.text = if (!mealContent.isNullOrBlank()) {
            mealContent
        } else {
            "등록된 식단이 없습니다."
        }

        binding.memoEt.setText(detail.memo.orEmpty())

        binding.waterAmountEt.setText(
            detail.waterIntakeMl
                ?.stripTrailingZeros()
                ?.toPlainString()
                .orEmpty()
        )

        selectedPreference = detail.satisfaction.toPreferenceCount()
        selectedIntakeStatus = detail.mealStatus.toMealStatusText()

        setPreferenceIcons(selectedPreference)
        setMealStatusSelected(selectedIntakeStatus.orEmpty())
        setSymptomsSelected(detail.symptoms)
        setMealImage(detail.imageUrl)
        renderDetailCapsules(detail.capsules)

        if (diaryMode == DiaryMode.READ) {
            setReadOnlyMode()
        }
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

    private fun initMealInfo() {
        val displayPetName = if (petName.isNullOrBlank()) "OO" else petName

        binding.mealOwnerTv.text = "${displayPetName}의 식단"
        binding.mealDateTv.text = getTodayDisplayDate()
        binding.pageTitleChipTv.text = mealTime.orEmpty()

        binding.mealFoodListTv.text = if (!mealContent.isNullOrBlank()) {
            mealContent
        } else {
            "등록된 식단이 없습니다."
        }
    }

    private fun initIntakeStatus() {
        selectedIntakeStatus = null

        setUnselectedStyle(binding.tvEatAll)
        setUnselectedStyle(binding.tvEatSome)
        setUnselectedStyle(binding.tvEatNone)

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
        selectedSymptoms.clear()

        val symptomViews = listOf(
            binding.tvSymptomItch to "가려움",
            binding.tvSymptomDiarrhea to "설사",
            binding.tvSymptomTired to "무기력",
            binding.tvSymptomVomit to "구토"
        )

        symptomViews.forEach { (view, symptom) ->
            updateMultiSelectView(view, false)

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
        selectedPreference = 0
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
    }

    private fun initSupplementInputs() {
        if (petId == -1L) {
            renderSupplementInputs(emptyList())
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getCapsuleIntakes(petId)

                if (response.isSuccessful) {
                    val body = response.body()

                    val supplements = body?.capsules?.map { capsule ->
                        SupplementUiModel(
                            id = capsule.petCapsuleId,
                            name = capsule.capsuleName,
                            amount = null
                        )
                    }.orEmpty()

                    renderSupplementInputs(supplements)
                } else {
                    Log.e("DiaryFragment", "영양제 조회 실패 code=${response.code()}")

                    Toast.makeText(
                        requireContext(),
                        "영양제 정보를 불러올 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    renderSupplementInputs(emptyList())
                }

            } catch (e: Exception) {
                Log.e("DiaryFragment", "영양제 조회 오류", e)

                Toast.makeText(
                    requireContext(),
                    "영양제 정보를 불러올 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()

                renderSupplementInputs(emptyList())
            }
        }
    }

    private fun renderDetailCapsules(capsules: List<MealDiaryDetailCapsuleResponse>) {
        val items = capsules.map { capsule ->
            SupplementUiModel(
                id = capsule.petCapsuleId,
                name = capsule.capsuleName ?: "영양제",
                amount = capsule.givenCount
            )
        }

        renderSupplementInputs(items)
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

    private fun setReadOnlyMode() {
        binding.memoEt.isEnabled = false
        binding.waterAmountEt.isEnabled = false

        binding.putFoodIv.visibility = View.GONE
        binding.mealRegisterBtn.visibility = View.GONE

        binding.imageUploadArea.isEnabled = false
        binding.mealPreviewIv.isEnabled = false
        binding.imagePlaceholderLayout.isEnabled = false

        binding.tvEatAll.isEnabled = false
        binding.tvEatSome.isEnabled = false
        binding.tvEatNone.isEnabled = false

        binding.tvSymptomItch.isEnabled = false
        binding.tvSymptomDiarrhea.isEnabled = false
        binding.tvSymptomTired.isEnabled = false
        binding.tvSymptomVomit.isEnabled = false

        binding.preference1Iv.isEnabled = false
        binding.preference2Iv.isEnabled = false
        binding.preference3Iv.isEnabled = false
        binding.preference4Iv.isEnabled = false
        binding.preference5Iv.isEnabled = false
    }

    private fun initMealLoadButton() {
        binding.putFoodIv.setOnClickListener {
            if (diaryMode == DiaryMode.READ) return@setOnClickListener

            if (petId == -1L) {
                Toast.makeText(
                    requireContext(),
                    "반려동물 정보를 확인할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (mealListCache.isNotEmpty()) {
                showMealLoadDialog()
            } else {
                loadMealWriteForm(showDialog = true)
            }
        }
    }

    private fun loadMealWriteForm(showDialog: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val today = getTodayApiDate()

                Log.d("DiaryFragment", "식단 조회 요청 petId=$petId, date=$today")

                val response = RetrofitClient.apiService.getMealWriteForm(
                    petId = petId,
                    date = today
                )

                Log.d("DiaryFragment", "식단 조회 응답 code=${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body == null) {
                        Toast.makeText(
                            requireContext(),
                            "식단 정보를 불러올 수 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    dailyDietId = body.dailyDietId
                    petName = body.petName

                    binding.mealOwnerTv.text = "${body.petName}의 식단"
                    binding.mealDateTv.text = formatApiDateToDisplayDate(body.dietDate)

                    mealListCache = body.meals.map { meal ->
                        MealItem(
                            mealId = meal.petMealScheduleId,
                            time = meal.mealTime.take(5),
                            content = meal.description,
                            isFed = false
                        )
                    }

                    if (mealListCache.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "등록된 식단이 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    autoBindCurrentMealId()

                    if (showDialog) {
                        showMealLoadDialog()
                    }

                } else {
                    val errorMessage = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        null
                    }

                    Log.e(
                        "DiaryFragment",
                        "식단 조회 실패 code=${response.code()}, error=$errorMessage"
                    )

                    Toast.makeText(
                        requireContext(),
                        "불러올 수 있는 식단이 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("DiaryFragment", "식단 조회 오류", e)

                Toast.makeText(
                    requireContext(),
                    "식단 정보를 불러올 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun autoBindCurrentMealId() {
        if (petMealScheduleId != -1L) return

        val matchedMeal = mealListCache.firstOrNull { meal ->
            !mealTime.isNullOrBlank() && meal.time == mealTime
        } ?: mealListCache.firstOrNull { meal ->
            !mealContent.isNullOrBlank() && meal.content == mealContent
        } ?: mealListCache.firstOrNull()

        if (matchedMeal != null) {
            applySelectedMeal(matchedMeal)
        }
    }

    private fun showMealLoadDialog() {
        val dialog = MealLoadDialogFragment(mealListCache) { selectedMeal ->
            applySelectedMeal(selectedMeal)
        }

        dialog.show(parentFragmentManager, "MealLoadDialog")
    }

    private fun applySelectedMeal(selectedMeal: MealItem) {
        petMealScheduleId = selectedMeal.mealId
        mealTime = selectedMeal.time
        mealContent = selectedMeal.content

        binding.pageTitleChipTv.text = selectedMeal.time
        binding.mealFoodListTv.text = selectedMeal.content

        Log.d(
            "DiaryFragment",
            "선택된 식단 dailyDietId=$dailyDietId, petMealScheduleId=$petMealScheduleId, time=$mealTime"
        )
    }

    private fun registerDiary() {
        if (petId == -1L) {
            Toast.makeText(
                requireContext(),
                "반려동물 정보를 확인할 수 없습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (dailyDietId == -1L || petMealScheduleId == -1L) {
            Toast.makeText(
                requireContext(),
                "등록할 식단 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.",
                Toast.LENGTH_SHORT
            ).show()

            loadMealWriteForm(showDialog = false)
            return
        }

        if (selectedPreference == 0) {
            Toast.makeText(
                requireContext(),
                "선호도를 선택해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (selectedIntakeStatus.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "섭취 상태를 선택해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val imageUri = selectedImageUri
        if (imageUri == null) {
            Toast.makeText(
                requireContext(),
                "식단 사진을 첨부해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val waterIntakeMl = binding.waterAmountEt.text.toString()
            .trim()
            .toBigDecimalOrNull() ?: 0.toBigDecimal()

        val memo = binding.memoEt.text.toString()
            .trim()
            .takeIf { it.isNotBlank() }

        val requestDto = MealDiaryCreateRequest(
            petId = petId,
            dailyDietId = dailyDietId,
            petMealScheduleId = petMealScheduleId,
            diaryDate = getTodayApiDate(),
            satisfaction = mapSatisfaction(selectedPreference),
            mealStatus = mapMealStatus(selectedIntakeStatus),
            waterIntakeMl = waterIntakeMl,
            memo = memo,
            symptoms = selectedSymptoms.map { mapSymptom(it) },
            capsules = getCapsuleRequestList()
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val json = Gson().toJson(requestDto)

                Log.d("DiaryFragment", "밥 일기 등록 request=$json")

                val requestBody = json.toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )

                val imagePart = createImagePart(imageUri)

                val response = RetrofitClient.apiService.createMealDiary(
                    request = requestBody,
                    image = imagePart
                )

                Log.d("DiaryFragment", "밥 일기 등록 응답 code=${response.code()}")

                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "밥 일기가 등록되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    parentFragmentManager.popBackStack()
                } else {
                    val errorMessage = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        null
                    }

                    Log.e(
                        "DiaryFragment",
                        "밥 일기 등록 실패 code=${response.code()}, error=$errorMessage"
                    )

                    Toast.makeText(
                        requireContext(),
                        "밥 일기를 등록할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("DiaryFragment", "밥 일기 등록 오류", e)

                Toast.makeText(
                    requireContext(),
                    "밥 일기 등록 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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

    private fun deleteDiary() {
        Toast.makeText(requireContext(), "삭제 완료", Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressedDispatcher.onBackPressed()
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
            if (diaryMode == DiaryMode.READ || diaryMode == DiaryMode.EDIT) {
                View.VISIBLE
            } else {
                View.GONE
            }

        editLayout.setOnClickListener {
            popupWindow.dismiss()

            if (mealDiaryId == -1L || petId == -1L) {
                Toast.makeText(
                    requireContext(),
                    "밥 일기 정보를 확인할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    DiaryFragment.newInstance(
                        mode = DiaryMode.EDIT,
                        petId = petId,
                        mealDiaryId = mealDiaryId,
                        petName = petName,
                        mealTime = mealTime,
                        mealContent = mealContent
                    )
                )
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

        popupWindow.showAsDropDown(
            binding.mealMoreIv,
            -dpToPx(150),
            dpToPx(8)
        )
    }

    private fun getCapsuleRequestList(): List<MealDiaryCapsuleRequest> {
        val result = mutableListOf<MealDiaryCapsuleRequest>()

        for (i in 0 until binding.supplementContainer.childCount) {
            val rowView = binding.supplementContainer.getChildAt(i)

            if (rowView is LinearLayout) {
                for (j in 0 until rowView.childCount) {
                    val child = rowView.getChildAt(j)

                    if (child is EditText) {
                        val petCapsuleId = child.tag as? Long ?: continue
                        val givenCount = child.text.toString()
                            .trim()
                            .toIntOrNull() ?: 0

                        result.add(
                            MealDiaryCapsuleRequest(
                                petCapsuleId = petCapsuleId,
                                givenCount = givenCount
                            )
                        )
                    }
                }
            }
        }

        return result
    }

    private fun createImagePart(uri: Uri): MultipartBody.Part {
        val file = uriToTempFile(uri)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            name = "image",
            filename = file.name,
            body = requestFile
        )
    }

    private fun uriToTempFile(uri: Uri): File {
        val fileName = getFileNameFromUri(uri)
        val tempFile = File(requireContext().cacheDir, fileName)

        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return tempFile
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = "meal_diary_${System.currentTimeMillis()}.jpg"

        val cursor = requireContext().contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && it.moveToFirst()) {
                fileName = it.getString(nameIndex)
            }
        }

        return fileName
    }

    private fun setPreferenceIcons(preferenceCount: Int) {
        val preferenceIconList = listOf(
            binding.preference1Iv,
            binding.preference2Iv,
            binding.preference3Iv,
            binding.preference4Iv,
            binding.preference5Iv
        )

        preferenceIconList.forEachIndexed { index, imageView ->
            if (index < preferenceCount) {
                imageView.setImageResource(R.drawable.icon_like)
            } else {
                imageView.setImageResource(R.drawable.icon_unlike)
            }
        }
    }

    private fun setMealStatusSelected(status: String) {
        setUnselectedStyle(binding.tvEatAll)
        setUnselectedStyle(binding.tvEatSome)
        setUnselectedStyle(binding.tvEatNone)

        when (status) {
            "다 먹음" -> setSelectedStyle(binding.tvEatAll)
            "조금 남김" -> setSelectedStyle(binding.tvEatSome)
            "안 먹음" -> setSelectedStyle(binding.tvEatNone)
        }
    }

    private fun setSymptomsSelected(symptoms: List<String>) {
        selectedSymptoms.clear()

        setUnselectedStyle(binding.tvSymptomItch)
        setUnselectedStyle(binding.tvSymptomDiarrhea)
        setUnselectedStyle(binding.tvSymptomTired)
        setUnselectedStyle(binding.tvSymptomVomit)

        symptoms.map { it.toSymptomText() }.forEach { symptom ->
            selectedSymptoms.add(symptom)

            when (symptom) {
                "가려움" -> setSelectedStyle(binding.tvSymptomItch)
                "설사" -> setSelectedStyle(binding.tvSymptomDiarrhea)
                "무기력" -> setSelectedStyle(binding.tvSymptomTired)
                "구토" -> setSelectedStyle(binding.tvSymptomVomit)
            }
        }
    }

    private fun setMealImage(imageUrl: String?) {
        if (imageUrl.isNullOrBlank()) {
            binding.mealPreviewIv.visibility = View.GONE
            binding.imagePlaceholderLayout.visibility = View.VISIBLE
            return
        }

        binding.mealPreviewIv.visibility = View.VISIBLE
        binding.imagePlaceholderLayout.visibility = View.GONE

        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.img_meal)
            .error(R.drawable.img_meal)
            .into(binding.mealPreviewIv)
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
        setPreferenceIcons(score)
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

    private fun mapSatisfaction(score: Int): String {
        return when (score) {
            1 -> "VERY_BAD"
            2 -> "BAD"
            3 -> "NORMAL"
            4 -> "GOOD"
            5 -> "VERY_GOOD"
            else -> "NORMAL"
        }
    }

    private fun mapMealStatus(status: String?): String {
        return when (status) {
            "다 먹음" -> "FINISHED"
            "조금 남김" -> "LEFT_SOME"
            "안 먹음" -> "NOT_EATEN"
            else -> "NOT_EATEN"
        }
    }

    private fun mapSymptom(symptom: String): String {
        return when (symptom) {
            "구토" -> "VOMITING"
            "무기력" -> "LETHARGY"
            "가려움" -> "ITCHING"
            "설사" -> "DIARRHEA"
            else -> symptom
        }
    }

    private fun String.toPreferenceCount(): Int {
        return when (this) {
            "VERY_BAD" -> 1
            "BAD" -> 2
            "NORMAL" -> 3
            "GOOD" -> 4
            "VERY_GOOD" -> 5
            else -> 0
        }
    }

    private fun String.toMealStatusText(): String {
        return when (this) {
            "FINISHED" -> "다 먹음"
            "LEFT_SOME" -> "조금 남김"
            "NOT_EATEN" -> "안 먹음"
            else -> this
        }
    }

    private fun String.toSymptomText(): String {
        return when (this) {
            "VOMITING" -> "구토"
            "LETHARGY" -> "무기력"
            "ITCHING" -> "가려움"
            "DIARRHEA" -> "설사"
            else -> this
        }
    }

    private fun getTodayApiDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date())
    }

    private fun getTodayDisplayDate(): String {
        return SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(Date())
    }

    private fun formatApiDateToDisplayDate(apiDate: String?): String {
        if (apiDate.isNullOrBlank()) return getTodayDisplayDate()

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
            val date = inputFormat.parse(apiDate)

            if (date != null) {
                outputFormat.format(date)
            } else {
                getTodayDisplayDate()
            }
        } catch (e: Exception) {
            getTodayDisplayDate()
        }
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
}