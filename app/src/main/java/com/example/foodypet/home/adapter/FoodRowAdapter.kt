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

class FoodRowAdapter(
    private val items: MutableList<FoodUiModel>,
    private val inventoryItems: List<String>
) : RecyclerView.Adapter<FoodRowAdapter.FoodRowViewHolder>() {

    private val units = listOf("g", "ml", "개", "봉")

    inner class FoodRowViewHolder(
        private val binding: ItemMealFoodRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var nameWatcher: TextWatcher? = null
        private var amountWatcher: TextWatcher? = null

        private val inventoryAdapter: ArrayAdapter<String> by lazy {
            ArrayAdapter(
                binding.root.context,
                R.layout.item_dropdown_food,
                inventoryItems.toMutableList()
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

            if (binding.unitDropdownActv.text?.toString() != item.unit) {
                binding.unitDropdownActv.setText(item.unit, false)
            }

            setupUnitDropdown(item)
            setupFoodAutoComplete(item)
            setupAmountEditText(item)
        }

        private fun setupStaticAdapters() {
            binding.foodNameActv.threshold = 1
            binding.foodNameActv.setAdapter(inventoryAdapter)

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

        private fun setupUnitDropdown(item: FoodUiModel) {
            binding.unitDropdownActv.setOnClickListener {
                binding.unitDropdownActv.showDropDown()
            }

            binding.unitDropdownActv.setOnItemClickListener { parent, _, position, _ ->
                item.unit = parent.getItemAtPosition(position).toString()
            }
        }

        private fun setupFoodAutoComplete(item: FoodUiModel) {
            binding.foodNameActv.setOnClickListener {
                val currentText = binding.foodNameActv.text?.toString().orEmpty()
                updateInventoryDropdown(currentText)
                if (inventoryAdapter.count > 0) {
                    binding.foodNameActv.showDropDown()
                }
            }

            binding.foodNameActv.setOnItemClickListener { parent, _, position, _ ->
                val selected = parent.getItemAtPosition(position).toString()
                item.name = selected
                binding.foodNameActv.setText(selected, false)
                binding.foodNameActv.setSelection(selected.length)
                binding.foodNameActv.dismissDropDown()
            }

            nameWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val keyword = s?.toString().orEmpty()
                    item.name = keyword

                    updateInventoryDropdown(keyword)

                    if (keyword.isNotBlank() && inventoryAdapter.count > 0) {
                        binding.foodNameActv.showDropDown()
                    } else {
                        binding.foodNameActv.dismissDropDown()
                    }
                }

                override fun afterTextChanged(s: Editable?) = Unit
            }

            binding.foodNameActv.addTextChangedListener(nameWatcher)
        }

        private fun updateInventoryDropdown(keyword: String) {
            val filtered = if (keyword.isBlank()) {
                inventoryItems
            } else {
                inventoryItems.filter {
                    it.contains(keyword, ignoreCase = true)
                }
            }

            inventoryAdapter.clear()
            inventoryAdapter.addAll(filtered)
            inventoryAdapter.notifyDataSetChanged()
        }

        private fun setupAmountEditText(item: FoodUiModel) {
            amountWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.amount = s?.toString().orEmpty()
                }

                override fun afterTextChanged(s: Editable?) = Unit
            }

            binding.foodAmountEt.addTextChangedListener(amountWatcher)
        }

        private fun removeWatchers() {
            nameWatcher?.let { binding.foodNameActv.removeTextChangedListener(it) }
            amountWatcher?.let { binding.foodAmountEt.removeTextChangedListener(it) }
            nameWatcher = null
            amountWatcher = null
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
}