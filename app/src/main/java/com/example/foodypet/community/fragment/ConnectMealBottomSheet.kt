package com.example.foodypet.community.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import com.example.foodypet.R
import com.example.foodypet.databinding.BottomSheetConnectMealBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.content.res.Configuration

class ConnectMealBottomSheet(
    private val onMealConnected: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetConnectMealBinding? = null
    private val binding get() = _binding!!

    private val petNames = listOf("랑이", "초코", "보리")
    private val feedTimes = listOf("8:00", "12:00", "18:00")

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
        setupFeedTimeDropdown()
        setupClickListeners()
    }

    private fun setupInitialView() {
        // 조회하기 누르기 전에는 식단 결과 숨김
        binding.layoutConnectedMealResult.visibility = View.INVISIBLE

        // 임시 기본값
        binding.petNameDropdownActv.setText("랑이", false)
        binding.feedTimeDropdownActv.setText("18:00", false)
    }

    private fun setupPetNameDropdown() {
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
            val selectedPetName = petNames[position]
            binding.petNameDropdownActv.setText(selectedPetName, false)

            // TODO: 이름 선택 시 급여일 조회 기능 연결
        }
    }

    private fun setupFeedTimeDropdown() {
        val feedTimeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_unit_dropdown,
            feedTimes
        )

        binding.feedTimeDropdownActv.setAdapter(feedTimeAdapter)

        binding.feedTimeDropdownActv.setOnClickListener {
            binding.feedTimeDropdownActv.showDropDown()
        }

        binding.feedTimeDropdownActv.setOnItemClickListener { _, _, position, _ ->
            val selectedFeedTime = feedTimes[position]
            binding.feedTimeDropdownActv.setText(selectedFeedTime, false)
        }
    }

    private fun setupClickListeners() {
        binding.tvSelectedFeedDate.setOnClickListener {
            showFeedDatePicker()
        }

        binding.btnSearchMeal.setOnClickListener {
            val selectedPetName = binding.petNameDropdownActv.text.toString()
            val selectedFeedDate = binding.tvSelectedFeedDate.text.toString()
            val selectedFeedTime = binding.feedTimeDropdownActv.text.toString()

            // TODO: selectedPetName, selectedFeedDate, selectedFeedTime 기준으로 식단 조회 API 연결

            showMealResult(
                mealDescription = "흑돼지 치즈볼 1개, 닭오돌뼈 10g, 플라그오프, 아가스틴 슈퍼부스트, 뉴로액트, 도란도란 단호박"
            )
        }

        binding.btnConnectMeal.setOnClickListener {
            // TODO: 식단 연결 API 연결

            // TODO: 서버 응답 성공 시 아래 코드 실행
            onMealConnected()
            dismiss()
        }
    }

    private fun showFeedDatePicker() {
        val koreanContext = getKoreanContext()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.ThemeOverlay_FoodyPet_MaterialDatePicker)
            .setTitleText("급여일 선택")
            .setPositiveButtonText("확인")
            .setNegativeButtonText("취소")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
            val formattedDate = dateFormat.format(Date(selectedDate))

            binding.tvSelectedFeedDate.text = formattedDate
            binding.tvSelectedFeedDate.setTextColor(
                requireContext().getColor(R.color.black)
            )

            binding.layoutConnectedMealResult.visibility = View.INVISIBLE
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

        // XML에 iv_meal_image src="@drawable/img_meal" 넣어둔 상태라
        // 임시 이미지는 그대로 보임.
        // 나중에 서버 이미지 연결하면 여기서 Glide로 넣으면 됨.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}