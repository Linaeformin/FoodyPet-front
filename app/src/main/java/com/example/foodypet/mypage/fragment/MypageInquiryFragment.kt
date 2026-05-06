package com.example.foodypet.mypage.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.foodypet.databinding.FragmentMypageInquiryBinding

class MypageInquiryFragment : Fragment() {

    private var _binding: FragmentMypageInquiryBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.mypageInquiryImageIv.setImageURI(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageInquiryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.mypageInquiryBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.mypageInquiryImageArea.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.mypageInquiryImageIv.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.mypageInquirySubmitBtn.setOnClickListener {
            validateInquiryInput()
        }
    }

    private fun validateInquiryInput() {
        val title = binding.mypageInquiryTitleEt.text.toString().trim()
        val content = binding.mypageInquiryContentEt.text.toString().trim()
        val email = binding.mypageInquiryEmailEt.text.toString().trim()

        when {
            title.isEmpty() -> {
                Toast.makeText(requireContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            content.isEmpty() -> {
                Toast.makeText(requireContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            email.isEmpty() -> {
                Toast.makeText(requireContext(), "답변 받을 이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(requireContext(), "올바른 이메일 형식으로 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            else -> {
                submitInquiry(
                    title = title,
                    content = content,
                    email = email,
                    imageUri = selectedImageUri
                )
            }
        }
    }

    private fun submitInquiry(
        title: String,
        content: String,
        email: String,
        imageUri: Uri?
    ) {
        // TODO: 서버 API 연결 후 문의하기 요청 보내기
        // title: 문의 제목
        // content: 문의 내용
        // email: 답변 받을 이메일
        // imageUri: 첨부 이미지 uri, 없으면 null

        Toast.makeText(requireContext(), "문의가 접수되었습니다.", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}