package com.example.foodypet.mypage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentPetRegisterStep2Binding

class PetRegisterStep2Fragment : Fragment() {

    private var _binding: FragmentPetRegisterStep2Binding? = null
    private val binding get() = _binding!!

    private var mode: String = MODE_REGISTER

    private var selectedSpecies = "강아지"
    private var selectedGender = "여"
    private var selectedNeutered = "완료"

    private val dogBreeds = listOf(
        "골든리트리버",
        "말티즈",
        "푸들",
        "포메라니안",
        "시츄",
        "비숑프리제",
        "치와와",
        "닥스훈트",
        "웰시코기",
        "시바견",
        "진돗개",
        "프렌치불독"
    )

    private val catBreeds = listOf(
        "코리안숏헤어",
        "러시안블루",
        "페르시안",
        "샴",
        "스코티시폴드",
        "먼치킨",
        "브리티시숏헤어",
        "아메리칸숏헤어",
        "뱅갈",
        "메인쿤",
        "랙돌",
        "터키시앙고라"
    )

    interface StepMoveListener {
        fun moveToStep(step: Int)
        fun moveToNextStep()
        fun moveToPrevStep()
        fun submitPetRegister()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments?.getString(ARG_MODE) ?: MODE_REGISTER
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetRegisterStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBreedDropdown(dogBreeds)
        initClickEvents()
        initIndicatorClickEvents()
        applyModeUi()
    }

    private fun applyModeUi() {
        if (mode == MODE_EDIT) {
            binding.petRegisterTitleTv.text = "반려동물 수정"
            binding.petRegisterEditSubmitBtn.visibility = View.VISIBLE
        } else {
            binding.petRegisterTitleTv.text = "반려동물 등록"
            binding.petRegisterEditSubmitBtn.visibility = View.GONE
        }
    }

    private fun initClickEvents() {
        binding.petRegisterBackIv.setOnClickListener {
            (parentFragment as? StepMoveListener)?.moveToPrevStep()
        }

        binding.petRegisterNextArea.setOnClickListener {
            (parentFragment as? StepMoveListener)?.moveToNextStep()
        }

        binding.petRegisterDogTv.setOnClickListener {
            selectedSpecies = "강아지"
            updateTwoButtonState(binding.petRegisterDogTv, binding.petRegisterCatTv)
            initBreedDropdown(dogBreeds)
        }

        binding.petRegisterCatTv.setOnClickListener {
            selectedSpecies = "고양이"
            updateTwoButtonState(binding.petRegisterCatTv, binding.petRegisterDogTv)
            initBreedDropdown(catBreeds)
        }

        binding.petRegisterFemaleTv.setOnClickListener {
            selectedGender = "여"
            updateTwoButtonState(binding.petRegisterFemaleTv, binding.petRegisterMaleTv)
        }

        binding.petRegisterMaleTv.setOnClickListener {
            selectedGender = "남"
            updateTwoButtonState(binding.petRegisterMaleTv, binding.petRegisterFemaleTv)
        }

        binding.petRegisterNeuteredDoneTv.setOnClickListener {
            selectedNeutered = "완료"
            updateTwoButtonState(
                binding.petRegisterNeuteredDoneTv,
                binding.petRegisterNeuteredNotDoneTv
            )
        }

        binding.petRegisterNeuteredNotDoneTv.setOnClickListener {
            selectedNeutered = "미완료"
            updateTwoButtonState(
                binding.petRegisterNeuteredNotDoneTv,
                binding.petRegisterNeuteredDoneTv
            )
        }

        binding.petRegisterEditSubmitBtn.setOnClickListener {
            (parentFragment as? StepMoveListener)?.submitPetRegister()
        }
    }

    private fun initIndicatorClickEvents() {
        if (mode != MODE_EDIT) return

        binding.petRegisterIndicator1Tv.setOnClickListener {
            (parentFragment as? StepMoveListener)?.moveToStep(0)
        }

        binding.petRegisterIndicator2Tv.setOnClickListener {
            (parentFragment as? StepMoveListener)?.moveToStep(1)
        }

        binding.petRegisterIndicator3Tv.setOnClickListener {
            (parentFragment as? StepMoveListener)?.moveToStep(2)
        }

        binding.petRegisterIndicator4Tv.setOnClickListener {
            (parentFragment as? StepMoveListener)?.moveToStep(3)
        }
    }

    private fun initBreedDropdown(breeds: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_food,
            breeds
        )

        binding.petRegisterBreedDropdownActv.setAdapter(adapter)
        binding.petRegisterBreedDropdownActv.setText(breeds.first(), false)

        binding.petRegisterBreedDropdownActv.setOnClickListener {
            binding.petRegisterBreedDropdownActv.showDropDown()
        }
    }

    private fun updateTwoButtonState(selected: TextView, unselected: TextView) {
        selected.setBackgroundResource(R.drawable.bg_orange_fill_8)
        selected.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        unselected.setBackgroundResource(R.drawable.bg_orange_stroke_8)
        unselected.setTextColor(ContextCompat.getColor(requireContext(), R.color.regular_orange))
    }

    fun getSpecies(): String {
        return selectedSpecies
    }

    fun getBreed(): String {
        return binding.petRegisterBreedDropdownActv.text.toString()
    }

    fun getGender(): String {
        return selectedGender
    }

    fun getNeutered(): String {
        return selectedNeutered
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODE = "mode"

        const val MODE_REGISTER = "register"
        const val MODE_EDIT = "edit"

        fun newInstance(mode: String): PetRegisterStep2Fragment {
            return PetRegisterStep2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, mode)
                }
            }
        }
    }
}