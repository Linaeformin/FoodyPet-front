package com.example.foodypet.mypage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.foodypet.databinding.FragmentPetRegisterStep4Binding

class PetRegisterStep4Fragment : Fragment() {

    private var _binding: FragmentPetRegisterStep4Binding? = null
    private val binding get() = _binding!!

    private var mode: String = MODE_REGISTER

    private lateinit var supplementNameEditTexts: List<EditText>
    private lateinit var supplementCountEditTexts: List<EditText>

    interface StepMoveListener {
        fun moveToStep(step: Int)
        fun moveToPrevStep()
        fun submitPetRegister()
    }

    data class SupplementInput(
        val name: String,
        val count: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments?.getString(ARG_MODE) ?: MODE_REGISTER
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetRegisterStep4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSupplementViews()
        initClickEvents()
        initIndicatorClickEvents()
        applyModeUi()
    }

    private fun applyModeUi() {
        if (mode == MODE_EDIT) {
            binding.petRegisterTitleTv.text = "반려동물 수정"
            binding.petRegisterSubmitBtn.text = "수정완료"
        } else {
            binding.petRegisterTitleTv.text = "반려동물 등록"
            binding.petRegisterSubmitBtn.text = "등록하기"
        }
    }

    private fun initSupplementViews() {
        supplementNameEditTexts = listOf(
            binding.petRegisterSupplementName1Et,
            binding.petRegisterSupplementName2Et,
            binding.petRegisterSupplementName3Et,
            binding.petRegisterSupplementName4Et,
            binding.petRegisterSupplementName5Et
        )

        supplementCountEditTexts = listOf(
            binding.petRegisterSupplementCount1Et,
            binding.petRegisterSupplementCount2Et,
            binding.petRegisterSupplementCount3Et,
            binding.petRegisterSupplementCount4Et,
            binding.petRegisterSupplementCount5Et
        )
    }

    private fun initClickEvents() {
        binding.petRegisterBackIv.setOnClickListener {
            (parentFragment as? StepMoveListener)?.moveToPrevStep()
        }

        binding.petRegisterSubmitBtn.setOnClickListener {
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

    fun getSupplements(): List<SupplementInput> {
        val result = mutableListOf<SupplementInput>()

        supplementNameEditTexts.forEachIndexed { index, nameEt ->
            val name = nameEt.text.toString().trim()
            val countText = supplementCountEditTexts[index].text.toString().trim()

            if (name.isNotEmpty()) {
                result.add(
                    SupplementInput(
                        name = name,
                        count = countText.toIntOrNull() ?: 0
                    )
                )
            }
        }

        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODE = "mode"

        const val MODE_REGISTER = "register"
        const val MODE_EDIT = "edit"

        fun newInstance(mode: String): PetRegisterStep4Fragment {
            return PetRegisterStep4Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, mode)
                }
            }
        }
    }
}