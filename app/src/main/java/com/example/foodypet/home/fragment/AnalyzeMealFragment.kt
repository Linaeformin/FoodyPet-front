package com.example.foodypet.home.fragment

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentAnalyzeMealBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.core.content.res.ResourcesCompat
import android.util.TypedValue

class AnalyzeMealFragment : Fragment() {

    private var _binding: FragmentAnalyzeMealBinding? = null
    private lateinit var currentMealAnalysis: MealAnalysisUiModel
    private val binding get() = _binding!!

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

        val mockResponse = getMockMealAnalysis()
        currentMealAnalysis = mockResponse
        renderMealAnalysis(mockResponse)
    }

    private fun initClickListeners() {
        binding.mealMoreToggleLl.setOnClickListener {
            showDetailSection()
        }

        // 카드 전체 눌러도 열리게 하고 싶으면 이거도 유지
        binding.mealMoreCardCv.setOnClickListener {
            showDetailSection()
        }

        binding.mealSubmitBtn.setOnClickListener {
            // TODO 최종 등록 API 호출
        }

        binding.mealBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showDetailSection() {
        binding.mealMoreCardCv.visibility = View.GONE
        binding.mealDetailContainerLl.visibility = View.VISIBLE

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
        val entries = items.map { PieEntry(it.percent.toFloat(), it.name) }
        val colors = items.map { Color.parseColor(it.colorHex) }

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

    /**
     * 오른쪽 범례는 서버 데이터 기준으로 동적 생성
     */
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
                setMargins(0, 4.dp, 8.dp, 4.dp)   // 16dp -> 8dp로 줄임
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
            StatusType.LOW -> Color.parseColor("#E53935")
            StatusType.HIGH -> Color.parseColor("#E53935")
        }
    }

    private fun formatPercent(value: Number): String {
        val doubleValue = value.toDouble()
        return if (doubleValue % 1.0 == 0.0) {
            doubleValue.toInt().toString()
        } else {
            String.format("%.1f", doubleValue)
        }
    }

    private fun getMockMealAnalysis(): MealAnalysisUiModel {
        return MealAnalysisUiModel(
            title = "랑이의 1회 식단 분석",
            chartItems = listOf(
                NutrientChartItem("단백질", 26, "#FF1A1A"),
                NutrientChartItem("수분", 10, "#FF1493"),
                NutrientChartItem("지방", 9, "#FF9800"),
                NutrientChartItem("조회분", 8, "#FFD400"),
                NutrientChartItem("조섬유", 5, "#66CC00"),
                NutrientChartItem("칼슘", 0.6, "#00B8D4"),
                NutrientChartItem("인", 0.5, "#3D5AFE"),
                NutrientChartItem("타우린", 0.1, "#8A2BE2"),
                NutrientChartItem("기타", 40.8, "#A9A9A9")
            ),
            calorie = NutrientBarItem(
                label = "칼로리",
                valueText = "220kcal",
                progressPercent = 95,
                status = StatusType.GOOD
            ),
            protein = NutrientBarItem(
                label = "단백질",
                valueText = "26%",
                progressPercent = 92,
                status = StatusType.GOOD
            ),
            fat = NutrientBarItem(
                label = "지방",
                valueText = "9%",
                progressPercent = 88,
                status = StatusType.GOOD
            ),
            carbohydrate = NutrientBarItem(
                label = "조회분",
                valueText = "8%",
                progressPercent = 35,
                status = StatusType.LOW
            ),
            fiber = NutrientBarItem(
                label = "조섬유",
                valueText = "5%",
                progressPercent = 78,
                status = StatusType.GOOD
            ),
            extraInfo = ExtraInfo(
                ratioTitle = "칼슘 : 인",
                ratioValue = "1.2 : 1",
                ratioStatus = StatusType.GOOD,
                taurineTitle = "타우린",
                taurineValue = "25mg",
                taurineStatus = StatusType.GOOD
            )
        )
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
    val percent: Number,
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
    LOW("부족"),
    HIGH("높음")
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()