package com.example.foodypet.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.databinding.DialogPopupSnackBinding
import com.example.foodypet.databinding.ItemMealBinding
import com.example.foodypet.home.adapter.FoodRowAdapter
import com.example.foodypet.home.model.FoodUiModel
import com.example.foodypet.home.model.MealItem
import com.example.foodypet.home.dto.SnackAutocompleteResponse
import com.example.foodypet.network.RetrofitClient
import com.example.foodypet.home.dto.PetTreatDiaryCreateRequest
import com.example.foodypet.home.dto.TreatDiaryCreateItemRequest
import kotlinx.coroutines.launch
import java.math.BigDecimal

class SnackDialogFragment : DialogFragment() {

    private var _binding: DialogPopupSnackBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodRowAdapter: FoodRowAdapter
    private lateinit var historyAdapter: SnackHistoryAdapter

    private var nextTreatRound: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPopupSnackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHeader()
        initSnackHistory()
        initSnackRegister()
        initClickListeners()
    }

    private fun initHeader() {
        binding.pageTitleChipTv.text = "${nextTreatRound}회"
    }

    private fun initSnackHistory() {
        historyAdapter = SnackHistoryAdapter(mutableListOf())

        binding.snackHistoryRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        loadTodayTreatDiaries()
    }

    private fun getPetId(): Long {
        return arguments?.getLong(ARG_PET_ID, -1L) ?: -1L
    }

    private fun loadTodayTreatDiaries() {
        val petId = getPetId()

        if (petId <= 0L) {
            Toast.makeText(
                requireContext(),
                "반려동물 정보를 찾을 수 없습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getTodayTreatDiaries(petId)

                if (response.isSuccessful) {
                    val treatDiaries = response.body().orEmpty()

                    nextTreatRound = treatDiaries.size + 1
                    binding.pageTitleChipTv.text = "${nextTreatRound}회"

                    val historyItems = treatDiaries.map { diary ->
                        MealItem(
                            mealId = diary.treatRound.toLong(),
                            time = "${diary.treatRound}회",
                            content = diary.items.joinToString(", ") { item ->
                                "${item.foodName} ${formatAmount(item.amount)}${item.unitLabel}"
                            },
                            isFed = true
                        )
                    }

                    historyAdapter.submitItems(historyItems)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "간식 기록을 불러오지 못했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "서버와 통신할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun formatAmount(amount: BigDecimal): String {
        return amount.stripTrailingZeros().toPlainString()
    }
    private fun initSnackRegister() {
        val foodItems = mutableListOf(
            FoodUiModel(unit = "GRAM", unitLabel = "g"),
            FoodUiModel(unit = "GRAM", unitLabel = "g"),
            FoodUiModel(unit = "GRAM", unitLabel = "g"),
            FoodUiModel(unit = "GRAM", unitLabel = "g")
        )

        foodRowAdapter = FoodRowAdapter(
            items = foodItems,
            lifecycleScope = viewLifecycleOwner.lifecycleScope,
            searchSnack = { keyword ->
                searchSnackAutocomplete(keyword)
            }
        )

        binding.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodRowAdapter
        }
    }

    private suspend fun searchSnackAutocomplete(
        keyword: String
    ): List<SnackAutocompleteResponse> {
        val response = RetrofitClient.apiService.searchSnackAutocomplete(keyword)

        return if (response.isSuccessful) {
            response.body().orEmpty()
        } else {
            emptyList()
        }
    }

    private fun initClickListeners() {
        binding.snackCloseIv.setOnClickListener {
            dismiss()
        }

        binding.snackSaveBtn.setOnClickListener {
            createTreatDiary()
        }
    }

    private fun createTreatDiary() {
        val petId = arguments?.getLong(ARG_PET_ID, -1L) ?: -1L

        if (petId <= 0L) {
            Toast.makeText(requireContext(), "반려동물 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = try {
            buildTreatDiaryCreateRequest()
        } catch (e: IllegalArgumentException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            return
        }

        binding.snackSaveBtn.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.createTreatDiary(
                    petId = petId,
                    request = request
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        response.body()?.message ?: "성공적으로 처리되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "간식 등록에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "서버와 통신할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.snackSaveBtn.isEnabled = true
            }
        }
    }

    private fun buildTreatDiaryCreateRequest(): PetTreatDiaryCreateRequest {
        val selectedItems = foodRowAdapter.getItems()
            .filter { item ->
                item.name.isNotBlank() || item.amount.isNotBlank()
            }

        if (selectedItems.isEmpty()) {
            throw IllegalArgumentException("등록할 간식을 1개 이상 입력해야 합니다.")
        }

        val requestItems = selectedItems.map { item ->
            val stockId = item.stockId
                ?: throw IllegalArgumentException("간식은 자동완성 목록에서 선택해야 합니다.")

            val amount = item.amount.toBigDecimalOrNull()
                ?: throw IllegalArgumentException("간식 급여량을 올바르게 입력해야 합니다.")

            if (amount <= BigDecimal.ZERO) {
                throw IllegalArgumentException("간식 급여량은 0보다 커야 합니다.")
            }

            TreatDiaryCreateItemRequest(
                stockId = stockId,
                amount = amount,
                unit = item.unit
            )
        }

        return PetTreatDiaryCreateRequest(
            treatRound = nextTreatRound,
            items = requestItems
        )
    }

    private fun getCurrentFoodItems(): List<FoodUiModel> {
        return foodRowAdapter.getItems()
            .filter { item ->
                item.name.isNotBlank() && item.amount.isNotBlank()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class SnackHistoryAdapter(
        private val items: MutableList<MealItem>
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

        fun submitItems(newItems: List<MealItem>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        class SnackHistoryViewHolder(
            private val binding: ItemMealBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(item: MealItem) {
                binding.quickMealTimeTv.text = item.time
                binding.quickMealContentTv.text = item.content
            }
        }
    }

    companion object {
        private const val ARG_PET_ID = "pet_id"

        fun newInstance(petId: Long): SnackDialogFragment {
            return SnackDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PET_ID, petId)
                }
            }
        }
    }
}