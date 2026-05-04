package com.example.foodypet.community.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.foodypet.R
import com.example.foodypet.community.adapter.CommunityPostImageAdapter
import com.example.foodypet.databinding.FragmentCommunityPostWriteBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer

class CommunityPostWriteFragment : Fragment() {

    private var _binding: FragmentCommunityPostWriteBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationView: BottomNavigationView? = null

    private lateinit var postImageAdapter: CommunityPostImageAdapter

    private val selectedImageUris = mutableListOf<Uri>()

    private val imagePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGE_COUNT)
        ) { uris ->

            if (uris.isEmpty()) {
                return@registerForActivityResult
            }

            selectedImageUris.clear()
            selectedImageUris.addAll(uris.take(MAX_IMAGE_COUNT))

            showSelectedImages()
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

        hideBottomNavigation()
        initImageViewPager()
        initClickListener()
    }

    private fun hideBottomNavigation() {
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun initImageViewPager() {
        postImageAdapter = CommunityPostImageAdapter(
            onImageClick = {
                openImagePicker()
            }
        )

        binding.postImageVp.adapter = postImageAdapter

        binding.postImageVp.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3

            val pageMargin = dpToPx(24) // 이미지 사이 간격
            val pageOffset = dpToPx(8)  // 옆 이미지 당겨오는 정도

            val transformer = CompositePageTransformer().apply {
                addTransformer(MarginPageTransformer(pageMargin))
                addTransformer { page, position ->
                    page.translationX += -position * pageOffset
                }
            }

            setPageTransformer(transformer)

            registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        updateIndicator(position)
                    }
                }
            )
        }
    }

    private fun initClickListener() {
        binding.postBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.postImageUploadArea.setOnClickListener {
            openImagePicker()
        }

        binding.btnConnectMeal.setOnClickListener {
            val bottomSheet = ConnectMealBottomSheet(
                onMealConnected = {
                    binding.btnConnectMeal.text = "식단 연결 수정"
                }
            )

            bottomSheet.show(parentFragmentManager, "ConnectMealBottomSheet")
        }

        binding.btnRegisterPost.setOnClickListener {
            val title = binding.postTitleEt.text.toString().trim()
            val content = binding.postContentEt.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "제목을 입력해줘.", Toast.LENGTH_SHORT).show()
                binding.postTitleEt.requestFocus()
                return@setOnClickListener
            }

            if (selectedImageUris.isEmpty()) {
                Toast.makeText(requireContext(), "이미지를 선택해줘.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "본문을 입력해줘.", Toast.LENGTH_SHORT).show()
                binding.postContentEt.requestFocus()
                return@setOnClickListener
            }

            // TODO: 게시글 등록 API 연결
            // title: 제목
            // content: 본문
            // selectedImageUris: 선택된 이미지 목록, 최대 6장

            Toast.makeText(requireContext(), "게시글 등록 준비 완료!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImagePicker() {
        imagePickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun showSelectedImages() {
        binding.postImageUploadArea.visibility = View.GONE
        binding.postImagePreviewArea.visibility = View.VISIBLE

        postImageAdapter.submitImages(selectedImageUris)

        binding.postImageVp.setCurrentItem(0, false)

        if (selectedImageUris.size > 1) {
            binding.postIndicatorArea.visibility = View.VISIBLE
            createIndicators(selectedImageUris.size)
            updateIndicator(0)
        } else {
            binding.postIndicatorArea.visibility = View.GONE
        }
    }

    private fun createIndicators(count: Int) {
        binding.postIndicatorArea.removeAllViews()

        repeat(count) { index ->
            val indicator = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(11),
                    dpToPx(11)
                ).apply {
                    if (index != 0) {
                        marginStart = dpToPx(10)
                    }
                }

                setBackgroundResource(R.drawable.indicator_unselect)
            }

            binding.postIndicatorArea.addView(indicator)
        }
    }

    private fun updateIndicator(selectedPosition: Int) {
        for (i in 0 until binding.postIndicatorArea.childCount) {
            val indicator = binding.postIndicatorArea.getChildAt(i)

            if (i == selectedPosition) {
                indicator.setBackgroundResource(R.drawable.indicator_select)
            } else {
                indicator.setBackgroundResource(R.drawable.indicator_unselect)
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        bottomNavigationView?.visibility = View.VISIBLE
        bottomNavigationView = null

        binding.postImageVp.adapter = null
        _binding = null
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 6
    }
}