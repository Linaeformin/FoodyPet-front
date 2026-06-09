package com.example.foodypet.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMealDiaryListBinding
import com.example.foodypet.home.adapter.MealDiaryAdapter
import com.example.foodypet.home.dto.MealDiaryTodayResponse
import com.example.foodypet.home.enum.DiaryMode
import com.example.foodypet.home.model.MealDiaryItem
import com.example.foodypet.network.RetrofitClient
import kotlinx.coroutines.launch

class MealDiaryListFragment : Fragment(R.layout.fragment_meal_diary_list) {

    private var _binding: FragmentMealDiaryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealDiaryAdapter: MealDiaryAdapter

    private var petId: Long = -1L
    private var petName: String? = null

    companion object {
        private const val ARG_PET_ID = "arg_pet_id"
        private const val ARG_PET_NAME = "arg_pet_name"

        fun newInstance(
            petId: Long,
            petName: String? = null
        ): MealDiaryListFragment {
            return MealDiaryListFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PET_ID, petId)
                    putString(ARG_PET_NAME, petName)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        petId = arguments?.getLong(ARG_PET_ID, -1L) ?: -1L
        petName = arguments?.getString(ARG_PET_NAME)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMealDiaryListBinding.bind(view)

        initRecyclerView()
        back()
        loadTodayMealDiaries()
    }

    private fun initRecyclerView() = with(binding) {
        mealDiaryAdapter = MealDiaryAdapter(
            onItemClick = { item ->
                moveToDiaryFragment(item)
            }
        )

        rvMealDiary.apply {
            adapter = mealDiaryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun loadTodayMealDiaries() {
        if (petId == -1L) {
            Toast.makeText(
                requireContext(),
                "반려동물 정보를 확인할 수 없습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getTodayMealDiaries(petId)

                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()

                    val mealDiaryList = body.map { diary ->
                        diary.toMealDiaryItem()
                    }

                    mealDiaryAdapter.submitList(mealDiaryList)

                    if (mealDiaryList.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "오늘 등록된 식단 기록이 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Log.e(
                        "MealDiaryListFragment",
                        "오늘 식단 기록 리스트 조회 실패 code=${response.code()}, error=${response.errorBody()?.string()}"
                    )

                    Toast.makeText(
                        requireContext(),
                        "식단 기록을 불러올 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("MealDiaryListFragment", "오늘 식단 기록 리스트 조회 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버 연결 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun MealDiaryTodayResponse.toMealDiaryItem(): MealDiaryItem {
        val foodDescription = dietSummary.ifBlank {
            foods.joinToString(", ") { food ->
                food.displayText
            }
        }.toDisplayFoodUnitText()

        return MealDiaryItem(
            mealDiaryId = mealDiaryId,
            petId = petId,
            time = mealTime.take(5),
            preferenceCount = satisfaction.toPreferenceCount(),
            status = mealStatus.toMealStatusText(),
            foodDesc = foodDescription,
            memo = memo.orEmpty(),
            imageUrl = imageUrl
        )
    }

    private fun String.toDisplayFoodUnitText(): String {
        return this
            .replace("GRAM", "g")
            .replace(" gram", "g")
            .replace(" Gram", "g")
            .replace("그램", "g")
    }

    private fun String.toPreferenceCount(): Int {
        return when (this) {
            "VERY_BAD" -> 1
            "BAD" -> 2
            "NORMAL" -> 3
            "GOOD" -> 4
            "VERY_GOOD" -> 5
            else -> 0
        }
    }

    private fun String.toMealStatusText(): String {
        return when (this) {
            "FINISHED" -> "다 먹음"
            "LEFT_SOME" -> "조금 남김"
            "NOT_EATEN" -> "안 먹음"
            else -> this
        }
    }

    private fun moveToDiaryFragment(item: MealDiaryItem) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                DiaryFragment.newInstance(
                    mode = DiaryMode.READ,
                    petId = item.petId,
                    mealDiaryId = item.mealDiaryId,
                    petName = petName,
                    mealTime = item.time,
                    mealContent = item.foodDesc
                )
            )
            .addToBackStack(null)
            .commit()
    }

    private fun back() {
        binding.mealBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}