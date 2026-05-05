package com.example.foodypet.mypage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMyPetBinding
import com.example.foodypet.mypage.adapter.MyPetAdapter
import com.example.foodypet.mypage.model.MyPet
import com.example.foodypet.mypage.model.PetGender

class MyPetFragment : Fragment() {

    private var _binding: FragmentMyPetBinding? = null
    private val binding get() = _binding!!

    private lateinit var myPetAdapter: MyPetAdapter

    private val petList = listOf(
        MyPet(
            name = "랑이",
            birth = "2022.04.07 생",
            type = "고양이",
            breed = "칼리코",
            isNeutered = true,
            gender = PetGender.GIRL,
            imageResId = R.drawable.cat_1,
            feedCount = "3회",
            feedTime = "7:00, 13:00, 20:00",
            supplement = "유산균 1정, 오메가3 2정"
        ),
        MyPet(
            name = "초코",
            birth = "2021.08.12 생",
            type = "강아지",
            breed = "말티즈",
            isNeutered = true,
            gender = PetGender.BOY,
            imageResId = R.drawable.cat_1,
            feedCount = "2회",
            feedTime = "8:00, 19:00",
            supplement = "관절 영양제 1정"
        ),
        MyPet(
            name = "나비",
            birth = "2023.01.20 생",
            type = "고양이",
            breed = "코리안숏헤어",
            isNeutered = false,
            gender = PetGender.GIRL,
            imageResId = R.drawable.cat_1,
            feedCount = "3회",
            feedTime = "9:00, 14:00, 21:00",
            supplement = "없음"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTopBar()
        setupViewPager()

        // 반려동물 개수 + 등록 카드 1개까지 포함
        setupIndicators(myPetAdapter.itemCount)
        updateIndicator(0)
    }

    private fun setupTopBar() {
        binding.myPetBackIv.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupViewPager() {
        myPetAdapter = MyPetAdapter(
            petList = petList,
            onEditClick = { pet ->
                // TODO: 프로필 수정 화면으로 이동
                // 예: findNavController().navigate(...)
            },
            onDeleteClick = { pet ->
                // TODO: 삭제 다이얼로그 또는 삭제 API 연결
            },
            onAddClick = {

            }
        )

        binding.myPetViewPager.adapter = myPetAdapter
        binding.myPetViewPager.offscreenPageLimit = 1

        binding.myPetViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateIndicator(position)
                }
            }
        )
    }

    private fun setupIndicators(count: Int) {
        binding.myPetIndicatorLayout.removeAllViews()

        repeat(count) { index ->
            val indicator = ImageView(requireContext()).apply {
                setImageResource(
                    if (index == 0) {
                        R.drawable.indicator_select
                    } else {
                        R.drawable.indicator_unselect
                    }
                )

                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(14),
                    dpToPx(14)
                ).apply {
                    marginStart = dpToPx(5)
                    marginEnd = dpToPx(5)
                }
            }

            binding.myPetIndicatorLayout.addView(indicator)
        }
    }

    private fun updateIndicator(position: Int) {
        for (i in 0 until binding.myPetIndicatorLayout.childCount) {
            val indicator = binding.myPetIndicatorLayout.getChildAt(i) as ImageView

            indicator.setImageResource(
                if (i == position) {
                    R.drawable.indicator_select
                } else {
                    R.drawable.indicator_unselect
                }
            )
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}