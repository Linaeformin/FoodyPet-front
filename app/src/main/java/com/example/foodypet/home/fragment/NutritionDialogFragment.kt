package com.example.foodypet.home.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.databinding.DialogTodayNutritionBinding
import com.example.foodypet.home.adapter.NutritionAdapter
import com.example.foodypet.home.model.NutritionUiModel

class NutritionDialogFragment(
    private val nutritionList: ArrayList<NutritionUiModel>,
    private val onSaveClick: (List<NutritionUiModel>) -> Unit
) : DialogFragment() {

    private var _binding: DialogTodayNutritionBinding? = null
    private val binding get() = _binding!!

    private lateinit var nutritionAdapter: NutritionAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        _binding = DialogTodayNutritionBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(binding.root)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        initView()
        initListener()

        return dialog
    }

    private fun initView() {
        nutritionAdapter = NutritionAdapter(nutritionList.toMutableList())

        binding.nutritionListRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = nutritionAdapter
        }
    }

    private fun initListener() {
        binding.dialogCloseBtn.setOnClickListener {
            dismiss()
        }

        binding.nutritionSaveBtn.setOnClickListener {
            onSaveClick(nutritionAdapter.getCurrentItems())
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}