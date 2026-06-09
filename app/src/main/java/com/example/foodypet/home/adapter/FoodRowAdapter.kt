package com.example.foodypet.home.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.databinding.ItemMealFoodRowBinding
import com.example.foodypet.home.model.FoodUiModel
import com.example.foodypet.home.dto.SnackAutocompleteResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FoodRowAdapter(
    private val items: MutableList<FoodUiModel>,
    private val lifecycleScope: CoroutineScope,
    private val searchSnack: suspend (String) -> List<SnackAutocompleteResponse>
) : RecyclerView.Adapter<FoodRowAdapter.FoodRowViewHolder>() {

    private val units = listOf("g", "ml", "개", "봉")

    inner class FoodRowViewHolder(
        private val binding: ItemMealFoodRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var nameWatcher: TextWatcher? = null
        private var amountWatcher: TextWatcher? = null
        private var searchJob: Job? = null

        private var currentSearchResults: List<SnackAutocompleteResponse> = emptyList()

        private val foodNameAdapter: ArrayAdapter<String> by lazy {
            ArrayAdapter(
                binding.root.context,
                R.layout.item_dropdown_food,
                mutableListOf()
            )
        }

        private val unitAdapter: ArrayAdapter<String> by lazy {
            ArrayAdapter(
                binding.root.context,
                R.layout.item_dropdown_unit,
                units
            )
        }

        init {
            setupRecyclerViewTouch()
            setupStaticAdapters()
        }

        fun bind(item: FoodUiModel) {
            removeWatchers()

            if (binding.foodNameActv.text?.toString() != item.name) {
                binding.foodNameActv.setText(item.name, false)
            }

            if (binding.foodAmountEt.text?.toString() != item.amount) {
                binding.foodAmountEt.setText(item.amount)
            }

            if (binding.unitDropdownActv.text?.toString() != item.unitLabel) {
                binding.unitDropdownActv.setText(item.unitLabel, false)
            }

            setupFoodAutoComplete(item)
            setupAmountEditText(item)
            setupUnitDropdown(item)
        }

        private fun setupStaticAdapters() {
            binding.foodNameActv.threshold = 1
            binding.foodNameActv.setAdapter(foodNameAdapter)

            binding.unitDropdownActv.setAdapter(unitAdapter)
        }

        private fun setupRecyclerViewTouch() {
            binding.foodNameActv.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                }
                false
            }

            binding.foodAmountEt.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                }
                false
            }

            binding.unitDropdownActv.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                }
                false
            }
        }

        private fun setupFoodAutoComplete(item: FoodUiModel) {
            binding.foodNameActv.setOnClickListener {
                val keyword = binding.foodNameActv.text?.toString().orEmpty()

                if (keyword.isNotBlank()) {
                    requestAutocomplete(keyword)
                }
            }

            binding.foodNameActv.setOnItemClickListener { parent, _, position, _ ->
                val selectedFoodName = parent.getItemAtPosition(position).toString()
                val selectedFood = currentSearchResults.find {
                    it.foodName == selectedFoodName
                }

                if (selectedFood != null) {
                    item.stockId = selectedFood.stockId
                    item.name = selectedFood.foodName
                    item.unit = selectedFood.unit
                    item.unitLabel = selectedFood.unitLabel

                    binding.foodNameActv.setText(selectedFood.foodName, false)
                    binding.foodNameActv.setSelection(selectedFood.foodName.length)

                    binding.unitDropdownActv.setText(selectedFood.unitLabel, false)

                    binding.foodNameActv.dismissDropDown()
                }
            }

            nameWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val keyword = s?.toString().orEmpty()

                    item.name = keyword

                    if (keyword.isBlank()) {
                        item.stockId = null
                        currentSearchResults = emptyList()
                        foodNameAdapter.clear()
                        foodNameAdapter.notifyDataSetChanged()
                        binding.foodNameActv.dismissDropDown()
                        return
                    }

                    requestAutocomplete(keyword)
                }

                override fun afterTextChanged(s: Editable?) = Unit
            }

            binding.foodNameActv.addTextChangedListener(nameWatcher)
        }

        private fun requestAutocomplete(keyword: String) {
            searchJob?.cancel()

            searchJob = lifecycleScope.launch {
                delay(250)

                val result = try {
                    searchSnack(keyword)
                } catch (e: Exception) {
                    emptyList()
                }

                currentSearchResults = result

                val foodNames = result.map { it.foodName }

                foodNameAdapter.clear()
                foodNameAdapter.addAll(foodNames)
                foodNameAdapter.notifyDataSetChanged()

                if (keyword.isNotBlank() && foodNameAdapter.count > 0) {
                    binding.foodNameActv.showDropDown()
                } else {
                    binding.foodNameActv.dismissDropDown()
                }
            }
        }

        private fun setupAmountEditText(item: FoodUiModel) {
            amountWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    item.amount = s?.toString().orEmpty()
                }

                override fun afterTextChanged(s: Editable?) = Unit
            }

            binding.foodAmountEt.addTextChangedListener(amountWatcher)
        }

        private fun setupUnitDropdown(item: FoodUiModel) {
            binding.unitDropdownActv.setOnClickListener {
                binding.unitDropdownActv.showDropDown()
            }

            binding.unitDropdownActv.setOnItemClickListener { parent, _, position, _ ->
                val selectedUnitLabel = parent.getItemAtPosition(position).toString()

                item.unitLabel = selectedUnitLabel
                item.unit = when (selectedUnitLabel) {
                    "g" -> "GRAM"
                    "ml" -> "ML"
                    "개" -> "COUNT"
                    "봉" -> "PACK"
                    else -> selectedUnitLabel
                }

                binding.unitDropdownActv.setText(selectedUnitLabel, false)
            }
        }

        private fun removeWatchers() {
            nameWatcher?.let {
                binding.foodNameActv.removeTextChangedListener(it)
            }

            amountWatcher?.let {
                binding.foodAmountEt.removeTextChangedListener(it)
            }

            nameWatcher = null
            amountWatcher = null
            searchJob?.cancel()
            searchJob = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodRowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMealFoodRowBinding.inflate(inflater, parent, false)
        return FoodRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodRowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: FoodRowViewHolder) {
        super.onViewRecycled(holder)
    }

    fun submitItems(newItems: MutableList<FoodUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getItems(): List<FoodUiModel> {
        return items
    }
}