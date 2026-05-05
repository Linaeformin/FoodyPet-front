package com.example.foodypet.mypage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodypet.databinding.FragmentPetRegisterBinding
import com.example.foodypet.mypage.adapter.PetRegisterPagerAdapter

class PetRegisterFragment : Fragment(),
    PetRegisterStep1Fragment.StepMoveListener,
    PetRegisterStep2Fragment.StepMoveListener,
    PetRegisterStep3Fragment.StepMoveListener,
    PetRegisterStep4Fragment.StepMoveListener {

    private var _binding: FragmentPetRegisterBinding? = null
    private val binding get() = _binding!!

    private var mode: String = MODE_REGISTER

    private lateinit var pagerAdapter: PetRegisterPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mode = arguments?.getString(ARG_MODE) ?: MODE_REGISTER
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewPager()
    }

    private fun setupViewPager() {
        pagerAdapter = PetRegisterPagerAdapter(
            fragment = this,
            mode = mode
        )

        binding.petRegisterViewPager.adapter = pagerAdapter
        binding.petRegisterViewPager.isUserInputEnabled = false
        binding.petRegisterViewPager.offscreenPageLimit = 4
    }

    override fun moveToStep(step: Int) {
        if (mode != MODE_EDIT) return

        binding.petRegisterViewPager.setCurrentItem(step, true)
    }

    override fun moveToNextStep() {
        val current = binding.petRegisterViewPager.currentItem

        if (current < 3) {
            binding.petRegisterViewPager.setCurrentItem(current + 1, true)
        }
    }

    override fun moveToPrevStep() {
        val current = binding.petRegisterViewPager.currentItem

        if (current > 0) {
            binding.petRegisterViewPager.setCurrentItem(current - 1, true)
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    override fun submitPetRegister() {
        if (mode == MODE_REGISTER) {
            // TODO: 등록 API 호출
        } else {
            // TODO: 수정 API 호출
        }

        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODE = "mode"

        const val MODE_REGISTER = "register"
        const val MODE_EDIT = "edit"

        fun newInstance(mode: String): PetRegisterFragment {
            return PetRegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, mode)
                }
            }
        }
    }
}