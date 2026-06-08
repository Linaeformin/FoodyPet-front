package com.example.foodypet.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentMealRecommendBinding
import com.example.foodypet.home.adapter.MealAdapter
import com.example.foodypet.home.dto.DietAnalysisResponse
import com.example.foodypet.home.dto.DietRecommendRequest
import com.example.foodypet.home.dto.DietRecommendResponse
import com.example.foodypet.home.dto.ErrorResponse
import com.example.foodypet.home.dto.NutrientBarResponse
import com.example.foodypet.home.dto.RecommendMealDto
import com.example.foodypet.home.model.MealItem
import com.example.foodypet.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.launch

class MealRecommendFragment : Fragment() {

    private var _binding: FragmentMealRecommendBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealAdapter: MealAdapter

    private var isTimeToggleOn = true

    private var petId: Long = -1L
    private var recommendResponse: DietRecommendResponse? = null
    private var recommendMeals: List<RecommendMealDto> = emptyList()

    companion object {
        private const val ARG_PET_ID = "petId"

        fun newInstance(petId: Long): MealRecommendFragment {
            return MealRecommendFragment().apply {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initView()
        initClickListener()
        requestRecommendDiet()
    }

    private fun initAdapter() {
        mealAdapter = MealAdapter()

        binding.mealRecommendRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealAdapter
            itemAnimator = null
            isNestedScrollingEnabled = false
        }
    }

    private fun initView() {
        binding.mealTitleTv.text = "식단 추천 받기"
        binding.mealReflectTitleTv.text = "반영 항목"

        binding.mealGenderTv.text = "성별"
        binding.mealWeightTv.text = "종"
        binding.mealNeuteredTv.text = "중성화"
        binding.mealAgeTv.text = "나이"

        updateTimeToggleUi()
    }

    private fun initClickListener() {
        binding.mealBackIv.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.timeToggleLayout.setOnClickListener {
            isTimeToggleOn = !isTimeToggleOn
            updateTimeToggleUi()
            renderRecommendMeals()
        }

        binding.btnAnalyze.setOnClickListener {
            requestDietAnalysis()
        }
    }

    private fun requestRecommendDiet() {
        if (petId == -1L) {
            Toast.makeText(requireContext(), "반려동물 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.recommendDiet(
                    DietRecommendRequest(
                        petId = petId
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        recommendResponse = body
                        recommendMeals = body.meals

                        binding.mealRecommendTitleTv.text = "${body.petName}의 추천 식단"
                        binding.mealFeedCountTv.text = "하루 ${body.meals.size}회 급여"

                        renderRecommendMeals()
                    } else {
                        Toast.makeText(requireContext(), "식단 추천 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = parseErrorMessage(
                        errorBody = response.errorBody()?.string(),
                        defaultMessage = "식단 추천에 실패했습니다."
                    )
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "식단 추천 요청 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestDietAnalysis() {
        val dietId = recommendResponse?.dailyDietId

        if (dietId == null) {
            Toast.makeText(requireContext(), "분석할 식단 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                AnalyzeMealFragment.newInstance(dietId)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun renderRecommendMeals() {
        val mealItems = recommendMeals.map { meal ->
            MealItem(
                mealId = meal.mealOrder.toLong(),
                time = if (isTimeToggleOn) {
                    meal.mealTime
                } else {
                    "${meal.mealOrder}회"
                },
                content = meal.description,
                isFed = false
            )
        }

        mealAdapter.submitList(mealItems)
    }

    private fun DietAnalysisResponse.toMealAnalysisUiModel(): MealAnalysisUiModel {
        val calorieBar = nutrientBars.findByName("칼로리")
        val proteinBar = nutrientBars.findByName("단백질")
        val fatBar = nutrientBars.findByName("지방")
        val ashBar = nutrientBars.findByName("조회분")
        val fiberBar = nutrientBars.findByName("조섬유")

        return MealAnalysisUiModel(
            title = title,
            chartItems = nutrientRatios.map {
                NutrientChartItem(
                    name = it.name,
                    percent = it.value,
                    colorHex = getNutrientColor(it.name)
                )
            },
            calorie = calorieBar.toNutrientBarItem("칼로리"),
            protein = proteinBar.toNutrientBarItem("단백질"),
            fat = fatBar.toNutrientBarItem("지방"),
            carbohydrate = ashBar.toNutrientBarItem("조회분"),
            fiber = fiberBar.toNutrientBarItem("조섬유"),
            extraInfo = ExtraInfo(
                ratioTitle = "칼슘 : 인",
                ratioValue = calciumPhosphorus.displayRatio,
                ratioStatus = calciumPhosphorus.status.toStatusType(),
                taurineTitle = "타우린",
                taurineValue = taurine.displayValue,
                taurineStatus = taurine.status.toStatusType()
            )
        )
    }

    private fun List<NutrientBarResponse>.findByName(name: String): NutrientBarResponse? {
        return firstOrNull { it.name == name }
    }

    private fun NutrientBarResponse?.toNutrientBarItem(defaultLabel: String): NutrientBarItem {
        return NutrientBarItem(
            label = this?.name ?: defaultLabel,
            valueText = this?.displayValue ?: "-",
            progressPercent = this?.percent?.toInt() ?: 0,
            status = this?.status?.toStatusType() ?: StatusType.GOOD
        )
    }

    private fun String.toStatusType(): StatusType {
        return when (uppercase()) {
            "GOOD" -> StatusType.GOOD
            "LACK" -> StatusType.LACK
            "EXCESS" -> StatusType.EXCESS
            else -> StatusType.GOOD
        }
    }

    private fun getNutrientColor(name: String): String {
        return when (name) {
            "단백질" -> "#FF1A1A"
            "수분" -> "#FF1493"
            "지방" -> "#FF9800"
            "조회분" -> "#FFD400"
            "조섬유" -> "#66CC00"
            "칼슘" -> "#00B8D4"
            "인" -> "#3D5AFE"
            "타우린" -> "#8A2BE2"
            "기타" -> "#A9A9A9"
            else -> "#A9A9A9"
        }
    }

    private fun updateTimeToggleUi() {
        if (isTimeToggleOn) {
            binding.timeToggleIv.setImageResource(R.drawable.icon_toggle_apply)
        } else {
            binding.timeToggleIv.setImageResource(R.drawable.icon_toggle_unapply)
        }
    }

    private fun parseErrorMessage(
        errorBody: String?,
        defaultMessage: String
    ): String {
        if (errorBody.isNullOrBlank()) {
            return defaultMessage
        }

        return try {
            Gson().fromJson(errorBody, ErrorResponse::class.java).message
                ?: defaultMessage
        } catch (e: Exception) {
            defaultMessage
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}