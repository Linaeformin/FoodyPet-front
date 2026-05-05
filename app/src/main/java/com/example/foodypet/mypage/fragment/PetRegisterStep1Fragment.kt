package com.example.foodypet.mypage.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.foodypet.databinding.FragmentPetRegisterStep1Binding

class PetRegisterStep1Fragment : Fragment() {

    private var _binding: FragmentPetRegisterStep1Binding? = null
    private val binding get() = _binding!!

    private var mode: String = MODE_REGISTER
    private var selectedImageUri: Uri? = null

    interface StepMoveListener {
        fun moveToStep(step: Int)
        fun moveToNextStep()
        fun moveToPrevStep()
        fun submitPetRegister()
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri

                binding.petRegisterSelectedImageIv.setImageURI(uri)
                binding.petRegisterSelectedImageIv.visibility = View.VISIBLE
                binding.petRegisterCameraIv.visibility = View.GONE
            }
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
        _binding = FragmentPetRegisterStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        applyModeUi()
        initClickEvents()
        initIndicatorClickEvents()
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

        binding.petRegisterCameraArea.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.petRegisterCameraIv.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.petRegisterSelectedImageIv.setOnClickListener {
            imagePickerLauncher.launch("image/*")
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

    fun getPetName(): String {
        return binding.petRegisterNameEt.text.toString()
    }

    fun getPetBirth(): String {
        return binding.petRegisterBirthEt.text.toString()
    }

    fun getSelectedImageUri(): Uri? {
        return selectedImageUri
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODE = "mode"

        const val MODE_REGISTER = "register"
        const val MODE_EDIT = "edit"

        fun newInstance(mode: String): PetRegisterStep1Fragment {
            return PetRegisterStep1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, mode)
                }
            }
        }
    }
}