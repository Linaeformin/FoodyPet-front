package com.example.foodypet.home.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodypet.databinding.DialogMealLoadBinding
import com.example.foodypet.home.adapter.MealAdapter
import com.example.foodypet.home.model.MealItem

class MealLoadDialogFragment(
    private val mealList: List<MealItem>,
    private val onMealClick: ((MealItem) -> Unit)? = null
) : DialogFragment() {

    private var _binding: DialogMealLoadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        _binding = DialogMealLoadBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(binding.root)

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        initView()

        return dialog
    }

    private fun initView() {
        val mealAdapter = MealAdapter { item ->
            onMealClick?.invoke(item)
            dismiss()
        }

        binding.rvMealLoad.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMealLoad.adapter = mealAdapter
        mealAdapter.submitList(mealList)

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.viewDim.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}