package com.example.foodypet.community.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.foodypet.community.adapter.CommunityPostImageAdapter
import com.example.foodypet.community.dto.CommunityPostCreateRequest
import com.example.foodypet.community.viewmodel.CommunityPostSharedViewModel
import com.example.foodypet.databinding.FragmentCommunityPostWriteBinding
import com.example.foodypet.network.MultipartUtil
import com.example.foodypet.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class CommunityPostWriteFragment : Fragment() {

    private var _binding: FragmentCommunityPostWriteBinding? = null
    private val binding get() = _binding!!

    private val communityPostSharedViewModel: CommunityPostSharedViewModel by activityViewModels()

    private lateinit var imageAdapter: CommunityPostImageAdapter

    private var selectedImageUri: Uri? = null
    private var selectedMealDiaryId: Long = -1L

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            showSelectedImage(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommunityPostWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupImageAdapter()
        initClickListener()
    }

    private fun setupImageAdapter() {
        imageAdapter = CommunityPostImageAdapter(
            onImageClick = {
                openImagePicker()
            }
        )

        binding.postImageVp.adapter = imageAdapter
        binding.postIndicatorArea.visibility = View.GONE
    }

    private fun initClickListener() {
        binding.postBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.postImageUploadArea.setOnClickListener {
            openImagePicker()
        }

        binding.btnConnectMeal.setOnClickListener {
            showConnectMealBottomSheet()
        }

        binding.btnRegisterPost.setOnClickListener {
            createCommunityPost()
        }
    }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    private fun showSelectedImage(uri: Uri) {
        binding.postImageUploadArea.visibility = View.GONE
        binding.postImagePreviewArea.visibility = View.VISIBLE
        binding.postIndicatorArea.visibility = View.GONE

        imageAdapter.submitImages(listOf(uri))
    }

    private fun showConnectMealBottomSheet() {
        val petItems = communityPostSharedViewModel.getCurrentPetItems()

        if (petItems.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "반려동물 정보를 불러올 수 없습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val bottomSheet = ConnectMealBottomSheet(
            petItems = petItems,
            onMealConnected = { mealDiaryId ->
                selectedMealDiaryId = mealDiaryId
                binding.btnConnectMeal.text = "식단 연결 완료"
            }
        )

        bottomSheet.show(parentFragmentManager, "ConnectMealBottomSheet")
    }

    private fun createCommunityPost() {
        val title = binding.postTitleEt.text.toString().trim()
        val content = binding.postContentEt.text.toString().trim()
        val imageUri = selectedImageUri

        if (title.isBlank()) {
            Toast.makeText(requireContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (content.isBlank()) {
            Toast.makeText(requireContext(), "본문을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedMealDiaryId == -1L) {
            Toast.makeText(requireContext(), "식단을 연결해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(requireContext(), "게시글 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                binding.btnRegisterPost.isEnabled = false

                val requestDto = CommunityPostCreateRequest(
                    title = title,
                    content = content,
                    mealDiaryId = selectedMealDiaryId
                )

                val requestJson = Gson().toJson(requestDto)

                val requestBody = requestJson.toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )

                val imagePart = MultipartUtil.createImagePart(
                    context = requireContext(),
                    uri = imageUri,
                    partName = "image"
                )

                Log.d(
                    "CommunityPostWriteFragment",
                    "게시글 등록 요청 request=$requestJson, imageUri=$imageUri"
                )

                val response = RetrofitClient.apiService.createCommunityPost(
                    request = requestBody,
                    image = imagePart
                )

                Log.d(
                    "CommunityPostWriteFragment",
                    "게시글 등록 응답 code=${response.code()}, isSuccessful=${response.isSuccessful}"
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        Toast.makeText(
                            requireContext(),
                            "게시글이 등록되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.d(
                            "CommunityPostWriteFragment",
                            "등록된 게시글 postId=${body.postId}"
                        )

                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "게시글 등록 응답이 비어 있습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()

                    Log.e(
                        "CommunityPostWriteFragment",
                        "게시글 등록 실패 code=${response.code()}, errorBody=$errorBody"
                    )

                    Toast.makeText(
                        requireContext(),
                        "게시글 등록에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("CommunityPostWriteFragment", "게시글 등록 통신 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버와 통신 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnRegisterPost.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}