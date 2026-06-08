package com.example.foodypet.home.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMealDiaryListBinding
import com.example.foodypet.home.adapter.MealDiaryAdapter
import com.example.foodypet.home.enum.DiaryMode
import com.example.foodypet.home.model.MealDiaryItem

class MealDiaryListFragment : Fragment(R.layout.fragment_meal_diary_list) {

    private var _binding: FragmentMealDiaryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealDiaryAdapter: MealDiaryAdapter

    private var petId: Long = -1L

    companion object {
        private const val ARG_PET_ID = "arg_pet_id"

        fun newInstance(petId: Long): MealDiaryListFragment {
            return MealDiaryListFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PET_ID, petId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        petId = arguments?.getLong(ARG_PET_ID, -1L) ?: -1L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMealDiaryListBinding.bind(view)

        initRecyclerView()
        back()
    }

    private fun initRecyclerView() = with(binding) {
        mealDiaryAdapter = MealDiaryAdapter(
            itemList = getDummyMealDiaryList(),
            onItemClick = {
                moveToDiaryFragment()
            }
        )

        rvMealDiary.apply {
            adapter = mealDiaryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun moveToDiaryFragment() {
        if (petId == -1L) {
            Toast.makeText(
                requireContext(),
                "반려동물 정보를 확인할 수 없습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                DiaryFragment.newInstance(
                    mode = DiaryMode.EDIT,
                    petId = petId
                )
            )
            .addToBackStack(null)
            .commit()
    }

    private fun getDummyMealDiaryList(): List<MealDiaryItem> {
        return listOf(
            MealDiaryItem(
                time = "9:00",
                preferenceCount = 4,
                status = "다 먹음",
                foodDesc = "흑돼지 치즈볼 1개, 닭오돌뼈 10g, 플라그오프 등",
                memo = "맛있게 먹기는 하는데 설사랑 구토를 하고 있음.",
                imageResId = R.drawable.img_meal
            ),
            MealDiaryItem(
                time = "13:00",
                preferenceCount = 4,
                status = "다 먹음",
                foodDesc = "흑돼지 치즈볼 1개, 닭오돌뼈 10g, 플라그오프 등",
                memo = "맛있게 먹기는 하는데 설사랑 구토를 하고 있음.",
                imageResId = R.drawable.img_meal
            ),
            MealDiaryItem(
                time = "16:00",
                preferenceCount = 4,
                status = "다 먹음",
                foodDesc = "흑돼지 치즈볼 1개, 닭오돌뼈 10g, 플라그오프 등",
                memo = "맛있게 먹기는 하는데 설사랑 구토를 하고 있음.",
                imageResId = R.drawable.img_meal
            )
        )
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