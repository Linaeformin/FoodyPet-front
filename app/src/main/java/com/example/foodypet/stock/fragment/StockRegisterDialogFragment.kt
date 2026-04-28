package com.example.foodypet.stock.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.foodypet.R
import com.example.foodypet.databinding.DialogStockRegisterBinding

class StockRegisterDialogFragment : DialogFragment() {

    private var _binding: DialogStockRegisterBinding? = null
    private val binding get() = _binding!!

    private var isSnackChecked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogStockRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val dialogWidth = (resources.displayMetrics.widthPixels * 0.86).toInt()

            setLayout(
                dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUnitDropdown()
        initClickListeners()
    }

    private fun initUnitDropdown() {
        val units = listOf("g", "ml", "개", "봉")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_unit,
            units
        )

        binding.unitDropdownActv.setAdapter(adapter)

        binding.unitDropdownActv.setOnClickListener {
            binding.unitDropdownActv.showDropDown()
        }

        binding.unitDropdownActv.setOnItemClickListener { _, _, position, _ ->
            binding.unitDropdownActv.setText(units[position], false)
        }
    }

    private fun initClickListeners() {
        binding.closeIv.setOnClickListener {
            dismiss()
        }

        binding.snackCheckIv.setOnClickListener {
            toggleSnackCheck()
        }

        binding.snackTv.setOnClickListener {
            toggleSnackCheck()
        }

        binding.stockAssignBtn.setOnClickListener {
            val productName = binding.productNameTv.text.toString()
            val expiredDate = binding.expiredDateEt.text.toString()
            val quantity = binding.quantityEt.text.toString()
            val unit = binding.unitDropdownActv.text.toString()

            // TODO: 여기서 서버 요청 or ViewModel 함수 호출하면 돼
            // 예시:
            // viewModel.registerStock(
            //     productName = productName,
            //     expiredDate = expiredDate,
            //     quantity = quantity.toInt(),
            //     unit = unit,
            //     isSnack = isSnackChecked
            // )

            dismiss()
        }
    }

    private fun toggleSnackCheck() {
        isSnackChecked = !isSnackChecked

        binding.snackCheckIv.setImageResource(
            if (isSnackChecked) {
                R.drawable.icon_square_check
            } else {
                R.drawable.icon_square_uncheck
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}