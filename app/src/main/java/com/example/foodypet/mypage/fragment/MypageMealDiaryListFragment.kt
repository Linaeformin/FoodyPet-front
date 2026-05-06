package com.example.foodypet.mypage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMypageMealDiaryListBinding
import com.example.foodypet.databinding.ItemMypageMealDiaryCalendarDayBinding
import com.example.foodypet.databinding.ItemMypageMealDiaryCalendarHeaderBinding
import com.example.foodypet.home.adapter.MealDiaryAdapter
import com.example.foodypet.home.enum.DiaryMode
import com.example.foodypet.home.fragment.DiaryFragment
import com.example.foodypet.home.model.MealDiaryItem
import com.example.foodypet.mypage.adapter.MypageMealDiaryPetAdapter
import com.example.foodypet.mypage.adapter.MypageMealDiaryPetItem
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

class MypageMealDiaryListFragment : Fragment() {

    private var _binding: FragmentMypageMealDiaryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var petAdapter: MypageMealDiaryPetAdapter

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
        // TODO: 서버에서 반려동물 목록 받아오면 이 부분만 교체
        val petList = listOf(
            MypageMealDiaryPetItem(
                petId = 1L,
                petName = "랑이"
            ),
            MypageMealDiaryPetItem(
                petId = 2L,
                petName = "우동"
            ),
            MypageMealDiaryPetItem(
                petId = 3L,
                petName = "초코"
            ),
            MypageMealDiaryPetItem(
                petId = 4L,
                petName = "보리"
            )
        )

        petAdapter.submitList(petList)

        if (petList.isNotEmpty()) {
            val firstPet = petList.first()

            selectedPetId = firstPet.petId
            petAdapter.setSelectedPetId(firstPet.petId)

            loadMealDiaryList()
        }
    }

    private fun loadMealDiaryList() {
        val petId = selectedPetId ?: return

        // TODO: 서버 연결 시 petId, selectedDate 넘기면 됨
        // ex) viewModel.getMealDiaryList(petId, selectedDate)

        val dummyMealDiaryList = getDummyMealDiaryList(petId)

        binding.mypageMealDiaryRv.layoutManager =
            LinearLayoutManager(requireContext())

        binding.mypageMealDiaryRv.adapter = MealDiaryAdapter(
            itemList = dummyMealDiaryList,
            onItemClick = { mealDiary ->
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        DiaryFragment.newInstance(DiaryMode.READ)
                    )
                    .addToBackStack(null)
                    .commit()
            }
        )
    }

    private fun getDummyMealDiaryList(petId: Long): List<MealDiaryItem> {
        return when (petId) {
            1L -> listOf(
                MealDiaryItem(
                    time = "9:00",
                    status = "다 먹음",
                    foodDesc = "흑돼지 치즈볼 1개, 닭오돌뼈 10g, 플라그오프 등",
                    memo = "랑이는 맛있게 먹기는 하는데 설사랑 구토를 하고 있음.",
                    imageResId = R.drawable.img_meal,
                    preferenceCount = 4
                ),
                MealDiaryItem(
                    time = "18:00",
                    status = "남김",
                    foodDesc = "닭가슴살 20g, 당근 5g, 사료 30g",
                    memo = "저녁은 조금 남김.",
                    imageResId = R.drawable.img_meal,
                    preferenceCount = 3
                )
            )

            2L -> listOf(
                MealDiaryItem(
                    time = "8:30",
                    status = "다 먹음",
                    foodDesc = "연어 큐브 2개, 사료 25g",
                    memo = "우동이는 연어를 좋아함.",
                    imageResId = R.drawable.img_meal,
                    preferenceCount = 5
                )
            )

            3L -> listOf(
                MealDiaryItem(
                    time = "10:00",
                    status = "조금 먹음",
                    foodDesc = "오리 고기 15g, 사료 20g",
                    memo = "초코는 입맛이 별로 없어 보임.",
                    imageResId = R.drawable.img_meal,
                    preferenceCount = 2
                )
            )

            4L -> listOf(
                MealDiaryItem(
                    time = "7:50",
                    status = "다 먹음",
                    foodDesc = "소고기 큐브 1개, 브로콜리 5g",
                    memo = "보리는 빠르게 다 먹음.",
                    imageResId = R.drawable.img_meal,
                    preferenceCount = 4
                )
            )

            else -> emptyList()
        }
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