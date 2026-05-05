package com.example.foodypet.mypage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentPetRegisterStep3Binding

class PetRegisterStep3Fragment : Fragment() {

    private var _binding: FragmentPetRegisterStep3Binding? = null
    private val binding get() = _binding!!

    private var mode: String = MODE_REGISTER
    private var selectedMealCount = 3

    private lateinit var mealCountButtons: List<TextView>
    private lateinit var mealTimeRows: List<View>
    private lateinit var mealTimeEditTexts: List<EditText>

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
        _binding = FragmentPetRegisterStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMealViews()
        initClickEvents()
        initIndicatorClickEvents()
        updateMealCountUi()
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

    private fun initMealViews() {
        mealCountButtons = listOf(
            binding.petRegisterMealCount1Tv,
            binding.petRegisterMealCount2Tv,
            binding.petRegisterMealCount3Tv,
            binding.petRegisterMealCount4Tv,
            binding.petRegisterMealCount5Tv,
            binding.petRegisterMealCount6Tv
        )

        mealTimeRows = listOf(
            binding.petRegisterMealTime1Row,
            binding.petRegisterMealTime2Row,
            binding.petRegisterMealTime3Row,
            binding.petRegisterMealTime4Row,
            binding.petRegisterMealTime5Row,
            binding.petRegisterMealTime6Row
        )

        mealTimeEditTexts = listOf(
            binding.petRegisterMealTime1Et,
            binding.petRegisterMealTime2Et,
            binding.petRegisterMealTime3Et,
            binding.petRegisterMealTime4Et,
            binding.petRegisterMealTime5Et,
            binding.petRegisterMealTime6Et
        )
    }

    private fun initClickEvents() {
        binding.petRegisterBackIv.setOnClickListener {
            (parentFragment as? StepMoveListener)?.moveToPrevStep()
        }

        binding.petRegisterNextArea.setOnClickListener {
            (parentFragment as? StepMoveListener)?.moveToNextStep()
        }

        mealCountButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectedMealCount = index + 1
                updateMealCountUi()
            }
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

    private fun updateMealCountUi() {
        mealCountButtons.forEachIndexed { index, button ->
            val isSelected = index + 1 == selectedMealCount

            button.setBackgroundResource(
                if (isSelected) R.drawable.bg_orange_fill_8
                else R.drawable.bg_orange_stroke_8
            )

            button.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isSelected) R.color.white else R.color.regular_orange
                )
            )
        }

        mealTimeRows.forEachIndexed { index, row ->
            row.visibility = if (index < selectedMealCount) View.VISIBLE else View.GONE
        }
    }

    fun getMealCount(): Int {
        return selectedMealCount
    }

    fun getMealTimes(): List<String> {
        return mealTimeEditTexts
            .take(selectedMealCount)
            .map { it.text.toString() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODE = "mode"

        const val MODE_REGISTER = "register"
        const val MODE_EDIT = "edit"

        fun newInstance(mode: String): PetRegisterStep3Fragment {
            return PetRegisterStep3Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, mode)
                }
            }
        }
    }
}