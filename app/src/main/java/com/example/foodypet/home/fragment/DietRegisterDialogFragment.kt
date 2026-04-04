package com.example.foodypet.home.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.foodypet.R
import com.example.foodypet.databinding.DialogDietRegisterBinding

class DietRegisterDialogFragment : DialogFragment() {

    private var _binding: DialogDietRegisterBinding? = null
    private val binding get() = _binding!!

    private var onRecommendClick: (() -> Unit)? = null
    private var onDirectClick: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDietRegisterBinding.inflate(LayoutInflater.from(requireContext()))

        binding.btnRecommend.setOnClickListener {
            onRecommendClick?.invoke()
            dismiss()
        }

        binding.btnDirect.setOnClickListener {
            onDirectClick?.invoke()
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun setOnRecommendClickListener(listener: () -> Unit) {
        onRecommendClick = listener
    }

    fun setOnDirectClickListener(listener: () -> Unit) {
        onDirectClick = listener
    }
}