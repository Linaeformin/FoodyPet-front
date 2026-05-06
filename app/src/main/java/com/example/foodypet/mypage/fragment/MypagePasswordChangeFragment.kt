package com.example.foodypet.mypage.fragment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMypagePasswordChangeBinding

class MypagePasswordChangeFragment : Fragment() {

    private var _binding: FragmentMypagePasswordChangeBinding? = null
    private val binding get() = _binding!!

    private var isNewPasswordVisible = false
    private var isNewPasswordConfirmVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypagePasswordChangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.mypagePasswordChangeBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.mypageNewPasswordEyeIv.setOnClickListener {
            isNewPasswordVisible = !isNewPasswordVisible

            setPasswordVisibility(
                isVisible = isNewPasswordVisible,
                editTextType = PasswordEditTextType.NEW_PASSWORD
            )
        }

        binding.mypageNewPasswordConfirmEyeIv.setOnClickListener {
            isNewPasswordConfirmVisible = !isNewPasswordConfirmVisible

            setPasswordVisibility(
                isVisible = isNewPasswordConfirmVisible,
                editTextType = PasswordEditTextType.NEW_PASSWORD_CONFIRM
            )
        }

        binding.mypagePasswordChangeSubmitBtn.setOnClickListener {
            validatePasswordInput()
        }
    }

    private fun setPasswordVisibility(
        isVisible: Boolean,
        editTextType: PasswordEditTextType
    ) {
        val editText = when (editTextType) {
            PasswordEditTextType.NEW_PASSWORD -> binding.mypageNewPasswordEt
            PasswordEditTextType.NEW_PASSWORD_CONFIRM -> binding.mypageNewPasswordConfirmEt
        }

        val eyeImageView = when (editTextType) {
            PasswordEditTextType.NEW_PASSWORD -> binding.mypageNewPasswordEyeIv
            PasswordEditTextType.NEW_PASSWORD_CONFIRM -> binding.mypageNewPasswordConfirmEyeIv
        }

        if (isVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            eyeImageView.setImageResource(R.drawable.icon_eye_open)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            eyeImageView.setImageResource(R.drawable.icon_eye_close)
        }

        editText.setSelection(editText.text.length)
    }

    private fun validatePasswordInput() {
        val currentPassword = binding.mypageCurrentPasswordEt.text.toString().trim()
        val newPassword = binding.mypageNewPasswordEt.text.toString().trim()
        val newPasswordConfirm = binding.mypageNewPasswordConfirmEt.text.toString().trim()

        when {
            currentPassword.isEmpty() -> {
                Toast.makeText(requireContext(), "현재 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            newPassword.isEmpty() -> {
                Toast.makeText(requireContext(), "새로운 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            newPasswordConfirm.isEmpty() -> {
                Toast.makeText(requireContext(), "새로운 비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            newPassword.length < 8 -> {
                Toast.makeText(requireContext(), "비밀번호는 8자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            newPassword != newPasswordConfirm -> {
                Toast.makeText(requireContext(), "새로운 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }

            currentPassword == newPassword -> {
                Toast.makeText(requireContext(), "현재 비밀번호와 다른 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            else -> {
                changePassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
            }
        }
    }

    private fun changePassword(
        currentPassword: String,
        newPassword: String
    ) {
        // TODO: 서버 API 연결 후 비밀번호 변경 요청 보내기
        // 예시:
        // viewModel.changePassword(currentPassword, newPassword)

        Toast.makeText(requireContext(), "비밀번호 변경 요청", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private enum class PasswordEditTextType {
        NEW_PASSWORD,
        NEW_PASSWORD_CONFIRM
    }
}