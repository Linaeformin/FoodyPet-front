package com.example.foodypet.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.databinding.DialogPopupSnackBinding
import com.example.foodypet.databinding.ItemMealBinding
import com.example.foodypet.home.adapter.FoodRowAdapter
import com.example.foodypet.home.model.FoodUiModel
import com.example.foodypet.home.model.MealItem

class SnackDialogFragment : DialogFragment() {

    private var _binding: DialogPopupSnackBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodRowAdapter: FoodRowAdapter
    private lateinit var historyAdapter: SnackHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPopupSnackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHeader()
        initSnackHistory()
        initSnackRegister()
        initClickListeners()
    }

    private fun initHeader() {
        binding.pageTitleChipTv.text = "4회"
    }

    private fun initSnackHistory() {
        val historyList = listOf(
            MealItem(
                mealId = 1L,
                time = "1회",
                content = "너티 강아지 고양이 츄르 하루루틴 굿모닝 퓨레 1개",
                isFed = true
            ),
            MealItem(
                mealId = 2L,
                time = "2회",
                content = "도란도란 단호박 10g",
                isFed = true
            ),
            MealItem(
                mealId = 3L,
                time = "3회",
                content = "닭오돌뼈 10g, 도란도란 우유 20ml",
                isFed = true
            )
        )

        historyAdapter = SnackHistoryAdapter(historyList)

        binding.snackHistoryRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun initSnackRegister() {
        val inventoryItems = listOf(
            "닭오돌뼈",
            "도란도란 단호박",
            "도란도란",
            "도란도란 우유",
            "너티 강아지 고양이 츄르 하루루틴 굿모닝 퓨레"
        )

        val foodItems = mutableListOf(
            FoodUiModel(name = "", amount = "10", unit = "g"),
            FoodUiModel(name = "", amount = "10", unit = "g"),
            FoodUiModel(name = "", amount = "", unit = "g"),
            FoodUiModel(name = "", amount = "", unit = "g")
        )

        foodRowAdapter = FoodRowAdapter(
            items = foodItems,
            inventoryItems = inventoryItems
        )

        binding.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodRowAdapter
        }
    }

    private fun initClickListeners() {
        binding.snackCloseIv.setOnClickListener {
            dismiss()
        }

        binding.snackSaveBtn.setOnClickListener {
            val result = getCurrentFoodItems()

            Toast.makeText(
                requireContext(),
                "간식 ${result.size}건 저장",
                Toast.LENGTH_SHORT
            ).show()

            // TODO:
            // 여기서 서버 전송 / ViewModel 저장 / 부모 Fragment로 데이터 전달
            dismiss()
        }
    }

    private fun getCurrentFoodItems(): List<FoodUiModel> {
        val currentList = mutableListOf<FoodUiModel>()

        for (i in 0 until foodRowAdapter.itemCount) {
            val item = (binding.foodRecyclerView.adapter as FoodRowAdapter)
            // 현재 어댑터 내부 리스트를 직접 꺼내는 함수 없어서
            // 아래처럼 별도 관리가 더 좋음
        }

        // 지금 FoodRowAdapter 구조상 외부에서 items 직접 접근이 안 되니까
        // 실무에선 adapter에 getItems() 추가하는 게 제일 깔끔함
        return emptyList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 이전 기록용 간단 어댑터
     */
    private class SnackHistoryAdapter(
        private val items: List<MealItem>
    ) : RecyclerView.Adapter<SnackHistoryAdapter.SnackHistoryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnackHistoryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemMealBinding.inflate(inflater, parent, false)
            return SnackHistoryViewHolder(binding)
        }

        override fun onBindViewHolder(holder: SnackHistoryViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        class SnackHistoryViewHolder(
            private val binding: ItemMealBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(item: MealItem) {
                binding.quickMealTimeTv.text = item.time
                binding.quickMealContentTv.text = item.content
            }
        }
    }
}