package com.example.foodypet.home.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.foodypet.databinding.DialogMealActionBinding

class MealActionDialogFragment(
    private val message: String,
    private val actionText: String,
    private val onActionClick: () -> Unit
) : DialogFragment() {

    private var _binding: DialogMealActionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogMealActionBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        binding.messageTv.text = message
        binding.actionTv.text = actionText

        binding.cancelTv.setOnClickListener {
            dismiss()
        }

        binding.actionTv.setOnClickListener {
            onActionClick.invoke()
            dismiss()
        }

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.5f)
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MealActionDialog"
    }
}