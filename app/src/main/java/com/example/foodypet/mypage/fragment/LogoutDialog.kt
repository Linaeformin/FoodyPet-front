package com.example.foodypet.ui.mypage

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.foodypet.LoginActivity
import com.example.foodypet.databinding.DialogLogoutBinding

class LogoutDialog : DialogFragment() {

    private var _binding: DialogLogoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogLogoutBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(true)

        initClickListener(dialog)

        return dialog
    }

    private fun initClickListener(dialog: Dialog) {
        binding.cancelTv.setOnClickListener {
            dialog.dismiss()
        }

        binding.actionTv.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)

            // MainActivity 스택 제거하고 LoginActivity로 이동
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            dialog.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.85).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)

            // 뒤 배경 어둡기 조절
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes = attributes.apply {
                dimAmount = 0.35f
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}