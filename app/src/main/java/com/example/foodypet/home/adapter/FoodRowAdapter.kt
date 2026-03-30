package com.example.foodypet.home.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
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

        fun bind(item: FoodUiModel) {
            removeWatchers()

            binding.foodNameActv.setText(item.name, false)
            binding.foodAmountEt.setText(item.amount)
            binding.unitDropdownActv.setText(item.unit, false)

            setupUnitDropdown(item)
            setupFoodAutoComplete(item)
            setupAmountEditText(item)
        }

        private fun setupUnitDropdown(item: FoodUiModel) {
            val unitAdapter = ArrayAdapter(
                binding.root.context,
                R.layout.item_dropdown_unit,
                units
            )

            binding.unitDropdownActv.setAdapter(unitAdapter)
            binding.unitDropdownActv.setOnClickListener {
                binding.unitDropdownActv.showDropDown()
            }

            binding.unitDropdownActv.setOnItemClickListener { parent, _, position, _ ->
                item.unit = parent.getItemAtPosition(position).toString()
            }
        }

        private fun setupFoodAutoComplete(item: FoodUiModel) {
            binding.foodNameActv.threshold = 1
            binding.foodNameActv.dropDownWidth = binding.foodNameActv.width

            val initialList = inventoryItems.toMutableList()

            val inventoryAdapter = ArrayAdapter(
                binding.root.context,
                R.layout.item_dropdown_food,
                initialList
            )
            binding.foodNameActv.setAdapter(inventoryAdapter)

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

                    val filtered = if (keyword.isBlank()) {
                        emptyList()
                    } else {
                        inventoryItems.filter {
                            it.contains(keyword, ignoreCase = true)
                        }
                    }

                    val newAdapter = ArrayAdapter(
                        binding.root.context,
                        R.layout.item_dropdown_food,
                        filtered
                    )
                    binding.foodNameActv.setAdapter(newAdapter)

                    if (filtered.isNotEmpty()) {
                        binding.foodNameActv.post {
                            binding.foodNameActv.requestFocus()
                            binding.foodNameActv.dropDownWidth = binding.foodNameActv.width
                            binding.foodNameActv.showDropDown()
                        }
                    } else {
                        binding.foodNameActv.dismissDropDown()
                    }
                }

                override fun afterTextChanged(s: Editable?) = Unit
            }

            binding.foodNameActv.addTextChangedListener(nameWatcher)
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
}