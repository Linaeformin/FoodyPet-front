package com.example.foodypet.mypage.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMypageMealDiaryListBinding
import com.example.foodypet.databinding.ItemMypageMealDiaryCalendarDayBinding
import com.example.foodypet.databinding.ItemMypageMealDiaryCalendarHeaderBinding
import com.example.foodypet.home.adapter.MealDiaryAdapter
import com.example.foodypet.home.dto.MealDiaryTodayResponse
import com.example.foodypet.home.enum.DiaryMode
import com.example.foodypet.home.fragment.DiaryFragment
import com.example.foodypet.home.model.MealDiaryItem
import com.example.foodypet.mypage.adapter.MypageMealDiaryPetAdapter
import com.example.foodypet.mypage.adapter.MypageMealDiaryPetItem
import com.example.foodypet.network.RetrofitClient
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

class MypageMealDiaryListFragment : Fragment() {

    private var _binding: FragmentMypageMealDiaryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var petAdapter: MypageMealDiaryPetAdapter
    private lateinit var mealDiaryAdapter: MealDiaryAdapter

    private var selectedPetId: Long? = null
    private var selectedLocalDate: LocalDate = LocalDate.now()
    private var selectedDate: String = LocalDate.now().toString()

    private val monthTitleFormatter = DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREA)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageMealDiaryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        initBackButton()
        initPetRecyclerView()
        initMealDiaryRecyclerView()
        initCalendarView()
        loadPetList()
    }

    private fun initBackButton() = with(binding) {
        mypageMealDiaryBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initPetRecyclerView() = with(binding) {
        petAdapter = MypageMealDiaryPetAdapter { selectedPet ->
            selectedPetId = selectedPet.petId
            loadMealDiaryList()
        }

        mypageMealDiaryPetRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        mypageMealDiaryPetRv.adapter = petAdapter
    }

    private fun initMealDiaryRecyclerView() = with(binding) {
        mealDiaryAdapter = MealDiaryAdapter(
            onItemClick = { mealDiary ->
                moveToDiaryFragment(mealDiary)
            }
        )

        mypageMealDiaryRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealDiaryAdapter
            setHasFixedSize(true)
        }
    }

    private fun initCalendarView() = with(binding) {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(12)
        val endMonth = currentMonth.plusMonths(12)
        val firstDayOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY).first()

        mypageMealDiaryCalendarView.dayBinder =
            object : MonthDayBinder<DayViewContainer> {
                override fun create(view: View): DayViewContainer {
                    return DayViewContainer(view)
                }

                override fun bind(
                    container: DayViewContainer,
                    data: CalendarDay
                ) {
                    container.bind(data)
                }
            }

        mypageMealDiaryCalendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthHeaderViewContainer> {
                override fun create(view: View): MonthHeaderViewContainer {
                    return MonthHeaderViewContainer(view)
                }

                override fun bind(
                    container: MonthHeaderViewContainer,
                    data: CalendarMonth
                ) {
                    container.bind(data)
                }
            }

        mypageMealDiaryCalendarView.setup(
            startMonth = startMonth,
            endMonth = endMonth,
            firstDayOfWeek = firstDayOfWeek
        )

        mypageMealDiaryCalendarView.scrollToMonth(currentMonth)
    }

    private fun loadPetList() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getTodayDiaries()

                if (response.isSuccessful) {
                    val body = response.body()

                    val petList = body?.pets?.map { pet ->
                        MypageMealDiaryPetItem(
                            petId = pet.petId,
                            petName = pet.petName
                        )
                    } ?: emptyList()

                    petAdapter.submitList(petList)

                    if (petList.isNotEmpty()) {
                        val firstPet = petList.first()

                        selectedPetId = firstPet.petId
                        petAdapter.setSelectedPetId(firstPet.petId)

                        loadMealDiaryList()
                    } else {
                        mealDiaryAdapter.submitList(emptyList())

                        Toast.makeText(
                            requireContext(),
                            "등록된 반려동물이 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Log.e(
                        "MypageMealDiaryListFragment",
                        "반려동물 목록 조회 실패 code=${response.code()}, error=${response.errorBody()?.string()}"
                    )

                    Toast.makeText(
                        requireContext(),
                        "반려동물 정보를 불러올 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("MypageMealDiaryListFragment", "반려동물 목록 조회 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버 연결 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadMealDiaryList() {
        val petId = selectedPetId ?: return

        if (selectedLocalDate != LocalDate.now()) {
            mealDiaryAdapter.submitList(emptyList())

            Toast.makeText(
                requireContext(),
                "현재 API는 오늘 식단 기록만 조회할 수 있습니다.",
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
                        "MypageMealDiaryListFragment",
                        "식단 기록 조회 실패 code=${response.code()}, error=${response.errorBody()?.string()}"
                    )

                    Toast.makeText(
                        requireContext(),
                        "식단 기록을 불러올 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("MypageMealDiaryListFragment", "식단 기록 조회 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버 연결 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun MealDiaryTodayResponse.toMealDiaryItem(): MealDiaryItem {
        return MealDiaryItem(
            mealDiaryId = mealDiaryId,
            petId = petId,
            time = mealTime.take(5),
            preferenceCount = satisfaction.toPreferenceCount(),
            status = mealStatus.toMealStatusText(),
            foodDesc = dietSummary.ifBlank {
                foods.joinToString(", ") { food ->
                    food.displayText
                }
            },
            memo = memo.orEmpty(),
            imageUrl = imageUrl
        )
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
            "LEFT" -> "남김"
            "SKIPPED" -> "안 먹음"
            else -> this
        }
    }

    private fun moveToDiaryFragment(mealDiary: MealDiaryItem) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                DiaryFragment.newInstance(
                    mode = DiaryMode.READ,
                    petId = mealDiary.petId,
                    mealDiaryId = mealDiary.mealDiaryId,
                    mealTime = mealDiary.time,
                    mealContent = mealDiary.foodDesc
                )
            )
            .addToBackStack(null)
            .commit()
    }

    inner class DayViewContainer(
        view: View
    ) : ViewContainer(view) {

        private val dayBinding = ItemMypageMealDiaryCalendarDayBinding.bind(view)

        fun bind(day: CalendarDay) = with(dayBinding) {
            val dayTextView = mypageMealDiaryCalendarDayTv

            if (day.position == DayPosition.MonthDate) {
                dayTextView.visibility = View.VISIBLE
                dayTextView.text = day.date.dayOfMonth.toString()
            } else {
                dayTextView.visibility = View.INVISIBLE
                dayTextView.text = ""
            }

            val isSelected = day.date == selectedLocalDate

            dayTextView.setBackgroundResource(
                if (isSelected && day.position == DayPosition.MonthDate) {
                    R.drawable.bg_orange_fill_circle
                } else {
                    0
                }
            )

            dayTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isSelected && day.position == DayPosition.MonthDate) {
                        R.color.white
                    } else {
                        R.color.black
                    }
                )
            )

            dayTextView.setOnClickListener {
                if (day.position != DayPosition.MonthDate) return@setOnClickListener

                val oldDate = selectedLocalDate

                selectedLocalDate = day.date
                selectedDate = day.date.toString()

                binding.mypageMealDiaryCalendarView.notifyDateChanged(oldDate)
                binding.mypageMealDiaryCalendarView.notifyDateChanged(selectedLocalDate)

                loadMealDiaryList()
            }
        }
    }

    inner class MonthHeaderViewContainer(
        view: View
    ) : ViewContainer(view) {

        private val headerBinding = ItemMypageMealDiaryCalendarHeaderBinding.bind(view)

        fun bind(month: CalendarMonth) = with(headerBinding) {
            mypageMealDiaryCalendarMonthTv.text =
                month.yearMonth.format(monthTitleFormatter)

            mypageMealDiaryCalendarPrevIv.setOnClickListener {
                val previousMonth = month.yearMonth.minusMonths(1)
                binding.mypageMealDiaryCalendarView.smoothScrollToMonth(previousMonth)
            }

            mypageMealDiaryCalendarNextIv.setOnClickListener {
                val nextMonth = month.yearMonth.plusMonths(1)
                binding.mypageMealDiaryCalendarView.smoothScrollToMonth(nextMonth)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}