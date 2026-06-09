package com.example.foodypet.community.fragment

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.foodypet.R
import com.example.foodypet.community.dto.ConnectMealPreviewRequest
import com.example.foodypet.community.dto.ConnectMealTimesRequest
import com.example.foodypet.community.model.ConnectMealPetItem
import com.example.foodypet.databinding.BottomSheetConnectMealBinding
import com.example.foodypet.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConnectMealBottomSheet(
    private val petItems: List<ConnectMealPetItem>,
    private val onMealConnected: (mealDiaryId: Long) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetConnectMealBinding? = null
    private val binding get() = _binding!!

    private var selectedPetId: Long = -1L
    private var selectedMealDate: String? = null
    private var selectedFeedTime: String? = null

    private var feedTimes: List<String> = emptyList()

    private var selectedMealDiaryId: Long = -1L
    private var selectedDailyDietId: Long = -1L

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            setOnShowListener { dialogInterface ->
                val dialog = dialogInterface as BottomSheetDialog

                val bottomSheet = dialog.findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )

                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(it)

                    it.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    it.requestLayout()

                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.skipCollapsed = true
                    behavior.isDraggable = true
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetConnectMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialView()
        setupPetNameDropdown()
        setupClickListeners()
    }

    private fun setupInitialView() {
        binding.layoutConnectedMealResult.visibility = View.INVISIBLE

        binding.feedTimeDropdownActv.setText("", false)
        binding.feedTimeDropdownActv.hint = "선택"
        binding.feedTimeDropdownActv.isEnabled = false

        if (petItems.isNotEmpty()) {
            val firstPet = petItems.first()
            selectedPetId = firstPet.petId
            binding.petNameDropdownActv.setText(firstPet.petName, false)
        } else {
            selectedPetId = -1L
            binding.petNameDropdownActv.setText("", false)
            Toast.makeText(requireContext(), "등록된 반려동물이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPetNameDropdown() {
        val petNames = petItems.map { it.petName }

        val petNameAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_unit_dropdown,
            petNames
        )

        binding.petNameDropdownActv.setAdapter(petNameAdapter)

        binding.petNameDropdownActv.setOnClickListener {
            binding.petNameDropdownActv.showDropDown()
        }

        binding.petNameDropdownActv.setOnItemClickListener { _, _, position, _ ->
            val selectedPet = petItems[position]

            selectedPetId = selectedPet.petId
            binding.petNameDropdownActv.setText(selectedPet.petName, false)

            clearSelectedMealPreview()
            clearFeedTimes()
            binding.layoutConnectedMealResult.visibility = View.INVISIBLE

            if (!selectedMealDate.isNullOrBlank()) {
                loadFeedTimes()
            }
        }
    }

    private fun loadMealPreview() {
        if (selectedPetId == -1L) {
            Toast.makeText(requireContext(), "반려동물을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val mealDate = selectedMealDate

        if (mealDate.isNullOrBlank()) {
            Toast.makeText(requireContext(), "급여일을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val feedTime = selectedFeedTime

        if (feedTime.isNullOrBlank()) {
            Toast.makeText(requireContext(), "급여 시간을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                binding.btnSearchMeal.isEnabled = false

                Log.d(
                    "ConnectMealBottomSheet",
                    "식단 미리보기 요청 petId=$selectedPetId, mealDate=$mealDate, mealTime=$feedTime"
                )

                val response = RetrofitClient.apiService.getConnectMealPreview(
                    ConnectMealPreviewRequest(
                        petId = selectedPetId,
                        mealDate = mealDate,
                        mealTime = feedTime
                    )
                )

                Log.d(
                    "ConnectMealBottomSheet",
                    "식단 미리보기 응답 code=${response.code()}, isSuccessful=${response.isSuccessful}"
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    Log.d(
                        "ConnectMealBottomSheet",
                        "식단 미리보기 body=$body"
                    )

                    if (body != null) {
                        selectedMealDiaryId = body.mealDiaryId
                        selectedDailyDietId = body.dailyDietId

                        val mealDescription = body.foods.joinToString(separator = ", ") { food ->
                            "${food.foodName} ${formatAmount(food.amount)}${convertUnit(food.unit)}"
                        }

                        if (mealDescription.isBlank()) {
                            clearSelectedMealPreview()
                            binding.layoutConnectedMealResult.visibility = View.INVISIBLE

                            Toast.makeText(
                                requireContext(),
                                "조회된 식단이 없습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            showMealResult(mealDescription)
                        }
                    } else {
                        clearSelectedMealPreview()
                        binding.layoutConnectedMealResult.visibility = View.INVISIBLE

                        Toast.makeText(
                            requireContext(),
                            "식단 조회 응답이 비어 있습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()

                    Log.e(
                        "ConnectMealBottomSheet",
                        "식단 미리보기 실패 code=${response.code()}, errorBody=$errorBody"
                    )

                    clearSelectedMealPreview()
                    binding.layoutConnectedMealResult.visibility = View.INVISIBLE

                    Toast.makeText(
                        requireContext(),
                        "식단 조회에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("ConnectMealBottomSheet", "식단 미리보기 통신 오류", e)

                clearSelectedMealPreview()
                binding.layoutConnectedMealResult.visibility = View.INVISIBLE

                Toast.makeText(
                    requireContext(),
                    "서버와 통신 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnSearchMeal.isEnabled = true
            }
        }
    }

    private fun setupFeedTimeDropdown(times: List<String>) {
        feedTimes = times

        val feedTimeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_unit_dropdown,
            feedTimes
        )

        binding.feedTimeDropdownActv.setAdapter(feedTimeAdapter)

        binding.feedTimeDropdownActv.isEnabled = feedTimes.isNotEmpty()

        if (feedTimes.isNotEmpty()) {
            selectedFeedTime = feedTimes.first()
            binding.feedTimeDropdownActv.setText(feedTimes.first(), false)
        } else {
            selectedFeedTime = null
            binding.feedTimeDropdownActv.setText("", false)
            binding.feedTimeDropdownActv.hint = "없음"
        }

        binding.feedTimeDropdownActv.setOnClickListener {
            if (feedTimes.isNotEmpty()) {
                binding.feedTimeDropdownActv.showDropDown()
            }
        }

        binding.feedTimeDropdownActv.setOnItemClickListener { _, _, position, _ ->
            val selectedTime = feedTimes[position]

            selectedFeedTime = selectedTime
            binding.feedTimeDropdownActv.setText(selectedTime, false)

            clearSelectedMealPreview()
            binding.layoutConnectedMealResult.visibility = View.INVISIBLE
        }
    }

    private fun setupClickListeners() {
        binding.tvSelectedFeedDate.setOnClickListener {
            showFeedDatePicker()
        }

        binding.btnSearchMeal.setOnClickListener {
            loadMealPreview()
        }

        binding.btnConnectMeal.setOnClickListener {
            if (selectedPetId == -1L) {
                Toast.makeText(requireContext(), "반려동물을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedMealDate.isNullOrBlank()) {
                Toast.makeText(requireContext(), "급여일을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedFeedTime.isNullOrBlank()) {
                Toast.makeText(requireContext(), "급여 시간을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedMealDiaryId == -1L || selectedDailyDietId == -1L) {
                Toast.makeText(requireContext(), "먼저 식단을 조회해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: 다음 식단 연결 API에서 selectedMealDiaryId, selectedDailyDietId 사용
            onMealConnected(selectedMealDiaryId)
            dismiss()
        }
    }

    private fun loadFeedTimes() {
        if (selectedPetId == -1L) {
            Toast.makeText(requireContext(), "반려동물을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val mealDate = selectedMealDate

        if (mealDate.isNullOrBlank()) {
            Toast.makeText(requireContext(), "급여일을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                binding.btnSearchMeal.isEnabled = false

                Log.d(
                    "ConnectMealBottomSheet",
                    "급여 시각 조회 요청 petId=$selectedPetId, mealDate=$mealDate"
                )

                val response = RetrofitClient.apiService.getConnectMealTimes(
                    ConnectMealTimesRequest(
                        petId = selectedPetId,
                        mealDate = mealDate
                    )
                )

                Log.d(
                    "ConnectMealBottomSheet",
                    "급여 시각 조회 응답 code=${response.code()}, isSuccessful=${response.isSuccessful}"
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    Log.d(
                        "ConnectMealBottomSheet",
                        "급여 시각 조회 body=$body"
                    )

                    if (body != null) {
                        val mealTimes = body.map { item ->
                            item.mealTime.take(5)
                        }

                        setupFeedTimeDropdown(mealTimes)

                        if (mealTimes.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "해당 날짜의 급여 시간이 없습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            binding.layoutConnectedMealResult.visibility = View.INVISIBLE
                        }
                    } else {
                        clearFeedTimes()
                        Toast.makeText(
                            requireContext(),
                            "급여 시간 응답이 비어 있습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()

                    Log.e(
                        "ConnectMealBottomSheet",
                        "급여 시각 조회 실패 code=${response.code()}, errorBody=$errorBody"
                    )

                    clearFeedTimes()
                    Toast.makeText(
                        requireContext(),
                        "급여 시간 조회에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("ConnectMealBottomSheet", "급여 시각 조회 통신 오류", e)

                clearFeedTimes()
                Toast.makeText(
                    requireContext(),
                    "서버와 통신 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnSearchMeal.isEnabled = true
            }
        }
    }

    private fun clearFeedTimes() {
        feedTimes = emptyList()
        selectedFeedTime = null

        binding.feedTimeDropdownActv.setAdapter(null)
        binding.feedTimeDropdownActv.setText("", false)
        binding.feedTimeDropdownActv.hint = "선택"
        binding.feedTimeDropdownActv.isEnabled = false
    }

    private fun clearSelectedMealPreview() {
        selectedMealDiaryId = -1L
        selectedDailyDietId = -1L
    }

    private fun showFeedDatePicker() {
        getKoreanContext()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.ThemeOverlay_FoodyPet_MaterialDatePicker)
            .setTitleText("급여일 선택")
            .setPositiveButtonText("확인")
            .setNegativeButtonText("취소")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val displayDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
            val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

            val date = Date(selectedDate)

            val displayDate = displayDateFormat.format(date)
            val apiDate = apiDateFormat.format(date)

            selectedMealDate = apiDate

            binding.tvSelectedFeedDate.text = displayDate
            binding.tvSelectedFeedDate.setTextColor(
                requireContext().getColor(R.color.black)
            )

            clearSelectedMealPreview()
            clearFeedTimes()
            binding.layoutConnectedMealResult.visibility = View.INVISIBLE

            loadFeedTimes()
        }

        datePicker.show(parentFragmentManager, "FeedDatePicker")
    }

    private fun getKoreanContext(): Context {
        val locale = Locale.KOREA
        Locale.setDefault(locale)

        val config = Configuration(requireContext().resources.configuration)
        config.setLocale(locale)

        return requireContext().createConfigurationContext(config)
    }

    private fun showMealResult(mealDescription: String) {
        binding.layoutConnectedMealResult.visibility = View.VISIBLE
        binding.tvMealDescription.text = mealDescription
    }

    private fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            String.format(Locale.KOREA, "%.2f", amount)
        }
    }

    private fun convertUnit(unit: String): String {
        return when (unit.uppercase()) {
            "GRAM" -> "g"
            "KG" -> "kg"
            "ML" -> "ml"
            "L" -> "L"
            "EA" -> "개"
            else -> unit
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}