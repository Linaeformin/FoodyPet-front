package com.example.foodypet.home.fragment

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentAnalyzeMealBinding
import com.example.foodypet.home.dto.DietAnalysisResponse
import com.example.foodypet.home.dto.ErrorResponse
import com.example.foodypet.home.dto.NutrientBarResponse
import com.example.foodypet.network.RetrofitClient
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import kotlinx.coroutines.launch

class AnalyzeMealFragment : Fragment() {

    private var _binding: FragmentAnalyzeMealBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentMealAnalysis: MealAnalysisUiModel

    private var dietId: Long = -1L

    companion object {
        private const val ARG_DIET_ID = "dietId"

        fun newInstance(dietId: Long): AnalyzeMealFragment {
            return AnalyzeMealFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DIET_ID, dietId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dietId = arguments?.getLong(ARG_DIET_ID, -1L) ?: -1L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzeMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPieChart(binding.mealPieChartPc)
        initClickListeners()
        requestDietAnalysis()
    }

    private fun initClickListeners() {
        binding.mealMoreToggleLl.setOnClickListener {
            showDetailSection()
        }

        binding.mealMoreCardCv.setOnClickListener {
            showDetailSection()
        }

        binding.mealSubmitBtn.setOnClickListener {
            confirmDiet()
        }

        binding.mealBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun confirmDiet() {
        if (dietId == -1L) {
            Toast.makeText(requireContext(), "등록할 식단 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.confirmDiet(dietId)

                if (response.isSuccessful) {
                    val body = response.body()

                    Toast.makeText(
                        requireContext(),
                        body?.message ?: "식단이 최종 등록되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                } else {
                    val errorMessage = parseErrorMessage(
                        response.errorBody()?.string(),
                        "식단 최종 등록에 실패했습니다."
                    )

                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "식단 최종 등록 요청 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestDietAnalysis() {
        if (dietId == -1L) {
            Toast.makeText(requireContext(), "분석할 식단 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getDietAnalysis(dietId)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        currentMealAnalysis = body.toMealAnalysisUiModel()
                        renderMealAnalysis(currentMealAnalysis)
                    } else {
                        Toast.makeText(requireContext(), "식단 분석 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = parseErrorMessage(
                        response.errorBody()?.string(),
                        "식단 분석에 실패했습니다."
                    )
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "식단 분석 요청 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun showDetailSection() {
        binding.mealMoreCardCv.visibility = View.GONE
        binding.mealDetailContainerLl.visibility = View.VISIBLE

        if (::currentMealAnalysis.isInitialized) {
            binding.mealDetailContainerLl.post {
                bindBarSection(
                    labelView = binding.mealFiberLabelTv,
                    valueView = binding.mealFiberValueTv,
                    statusView = binding.mealFiberStatusTv,
                    statusIconView = binding.mealFiberStatusIv,
                    fillView = binding.mealFiberFillV,
                    item = currentMealAnalysis.fiber
                )
            }
        }
    }

    private fun initPieChart(pieChart: PieChart) {
        pieChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 68f
            transparentCircleRadius = 72f
            setHoleColor(Color.WHITE)
            setTransparentCircleAlpha(0)
            setUsePercentValues(false)
            setDrawEntryLabels(false)
            setDrawCenterText(false)
            setExtraOffsets(0f, 0f, 0f, 0f)
            rotationAngle = 0f
            isRotationEnabled = false
            setTouchEnabled(false)
        }
    }

    private fun renderMealAnalysis(data: MealAnalysisUiModel) {
        binding.mealAnalysisTitleTv.text = data.title

        renderPieChart(data.chartItems)
        renderLegendItems(data.chartItems)

        bindBarSection(
            labelView = binding.mealCalorieLabelTv,
            valueView = binding.mealCalorieValueTv,
            statusView = binding.mealCalorieStatusTv,
            statusIconView = binding.mealCalorieStatusIv,
            fillView = binding.mealCalorieFillV,
            item = data.calorie
        )

        bindBarSection(
            labelView = binding.mealProteinLabelTv,
            valueView = binding.mealProteinValueTv,
            statusView = binding.mealProteinStatusTv,
            statusIconView = binding.mealProteinStatusIv,
            fillView = binding.mealProteinFillV,
            item = data.protein
        )

        bindBarSection(
            labelView = binding.mealFatLabelTv,
            valueView = binding.mealFatValueTv,
            statusView = binding.mealFatStatusTv,
            statusIconView = binding.mealFatStatusIv,
            fillView = binding.mealFatFillV,
            item = data.fat
        )

        bindBarSection(
            labelView = binding.mealCarbohydrateLabelTv,
            valueView = binding.mealCarbohydrateValueTv,
            statusView = binding.mealCarbohydrateStatusTv,
            statusIconView = binding.mealCarbohydrateStatusIv,
            fillView = binding.mealCarbohydrateFillV,
            item = data.carbohydrate
        )

        bindBarSection(
            labelView = binding.mealFiberLabelTv,
            valueView = binding.mealFiberValueTv,
            statusView = binding.mealFiberStatusTv,
            statusIconView = binding.mealFiberStatusIv,
            fillView = binding.mealFiberFillV,
            item = data.fiber
        )

        binding.mealRatioLabelTv.text = data.extraInfo.ratioTitle
        binding.mealRatioValueTv.text = data.extraInfo.ratioValue
        binding.mealRatioStatusTv.text = data.extraInfo.ratioStatus.text
        binding.mealRatioStatusTv.setTextColor(Color.parseColor("#222222"))
        binding.mealRatioStatusIv.imageTintList =
            ColorStateList.valueOf(getStatusColor(data.extraInfo.ratioStatus))

        binding.mealTaurineLabelTv.text = data.extraInfo.taurineTitle
        binding.mealTaurineValueTv.text = data.extraInfo.taurineValue
        binding.mealTaurineStatusTv.text = data.extraInfo.taurineStatus.text
        binding.mealTaurineStatusTv.setTextColor(Color.parseColor("#222222"))
        binding.mealTaurineStatusIv.imageTintList =
            ColorStateList.valueOf(getStatusColor(data.extraInfo.taurineStatus))
    }

    private fun renderPieChart(items: List<NutrientChartItem>) {
        val entries = items.map {
            PieEntry(it.percent.toFloat(), it.name)
        }

        val colors = items.map {
            Color.parseColor(it.colorHex)
        }

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            sliceSpace = 2f
            selectionShift = 0f
        }

        val pieData = PieData(dataSet).apply {
            setDrawValues(false)
        }

        binding.mealPieChartPc.data = pieData
        binding.mealPieChartPc.invalidate()
        binding.mealPieChartPc.animateY(700, Easing.EaseInOutQuad)
    }

    private fun renderLegendItems(items: List<NutrientChartItem>) {
        binding.mealNutrientLegendGl.removeAllViews()

        items.forEach { item ->
            binding.mealNutrientLegendGl.addView(createLegendItemView(item))
        }
    }

    private fun createLegendItemView(item: NutrientChartItem): View {
        val context = requireContext()

        val rowLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(0, 4.dp, 8.dp, 4.dp)
            }
        }

        val dotView = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(6.dp, 6.dp).apply {
                marginEnd = 4.dp
            }
            background = ContextCompat.getDrawable(context, R.drawable.bg_meal_legend_dot)
            background?.setTint(Color.parseColor(item.colorHex))
        }

        val textView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            text = "${item.name}(${formatPercent(item.percent)}%)"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            setTextColor(Color.parseColor("#444444"))
            typeface = ResourcesCompat.getFont(context, R.font.scdream_light)
            includeFontPadding = false
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        rowLayout.addView(dotView)
        rowLayout.addView(textView)

        return rowLayout
    }

    private fun bindBarSection(
        labelView: TextView,
        valueView: TextView,
        statusView: TextView,
        statusIconView: ImageView,
        fillView: View,
        item: NutrientBarItem
    ) {
        labelView.text = item.label
        valueView.text = item.valueText
        statusView.text = item.status.text
        statusView.setTextColor(Color.parseColor("#222222"))
        statusIconView.imageTintList = ColorStateList.valueOf(getStatusColor(item.status))

        fillView.post {
            val parentWidth = (fillView.parent as View).width
            val progress = item.progressPercent.coerceIn(0, 100)
            val targetWidth = (parentWidth * (progress / 100f)).toInt()

            fillView.layoutParams = fillView.layoutParams.apply {
                width = targetWidth
            }
            fillView.requestLayout()
        }
    }

    private fun getStatusColor(status: StatusType): Int {
        return when (status) {
            StatusType.GOOD -> Color.parseColor("#57B600")
            StatusType.LACK -> Color.parseColor("#E53935")
            StatusType.EXCESS -> Color.parseColor("#E53935")
        }
    }

    private fun formatPercent(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            String.format("%.1f", value)
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

data class MealAnalysisUiModel(
    val title: String,
    val chartItems: List<NutrientChartItem>,
    val calorie: NutrientBarItem,
    val protein: NutrientBarItem,
    val fat: NutrientBarItem,
    val carbohydrate: NutrientBarItem,
    val fiber: NutrientBarItem,
    val extraInfo: ExtraInfo
)

data class NutrientChartItem(
    val name: String,
    val percent: Double,
    val colorHex: String
)

data class NutrientBarItem(
    val label: String,
    val valueText: String,
    val progressPercent: Int,
    val status: StatusType
)

data class ExtraInfo(
    val ratioTitle: String,
    val ratioValue: String,
    val ratioStatus: StatusType,
    val taurineTitle: String,
    val taurineValue: String,
    val taurineStatus: StatusType
)

enum class StatusType(val text: String) {
    GOOD("양호"),
    LACK("부족"),
    EXCESS("많음")
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()