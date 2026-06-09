package com.example.foodypet.home.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.foodypet.R

class WaterAmountDialog(
    context: Context,
    private val initialValues: List<Int> = emptyList(),
    private val onSaveClick: ((totalAmount: Int, inputValues: List<Int>) -> Unit)? = null
) : Dialog(context) {

    private lateinit var closeIv: ImageView
    private lateinit var inputContainerLl: LinearLayout
    private lateinit var addToggleLl: LinearLayout
    private lateinit var totalTv: TextView
    private lateinit var saveBtn: TextView

    private val inputEditTextList = mutableListOf<EditText>()
    private val defaultInputRowList = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_water_amount)
        setCancelable(true)

        initWindow()
        initView()
        initDefaultInputs()
        applyInitialValues()
        initListener()
        updateTotal()
    }

    private fun initWindow() {
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val params = attributes
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            attributes = params
        }
    }

    private fun initView() {
        closeIv = findViewById(R.id.water_dialog_close_iv)
        inputContainerLl = findViewById(R.id.water_input_container_ll)
        addToggleLl = findViewById(R.id.meal_more_toggle_ll)
        totalTv = findViewById(R.id.water_total_tv)
        saveBtn = findViewById(R.id.water_save_btn)
    }

    private fun initDefaultInputs() {
        val inputEt1 = findViewById<EditText>(R.id.input_et)
        val inputEt2 = findViewById<EditText>(R.id.input_et_2)
        val inputEt3 = findViewById<EditText>(R.id.input_et_3)

        inputEditTextList.clear()
        defaultInputRowList.clear()

        inputEditTextList.add(inputEt1)
        inputEditTextList.add(inputEt2)
        inputEditTextList.add(inputEt3)

        defaultInputRowList.add(findViewById(R.id.water_input_row_1))
        defaultInputRowList.add(findViewById(R.id.water_input_row_2))
        defaultInputRowList.add(findViewById(R.id.water_input_row_3))

        inputEt1.setText("")
        inputEt2.setText("")
        inputEt3.setText("")

        addTextWatcher(inputEt1)
        addTextWatcher(inputEt2)
        addTextWatcher(inputEt3)
    }

    private fun applyInitialValues() {
        if (initialValues.isEmpty()) {
            showDefaultEmptyInputs()
            updateTotal()
            return
        }

        hideAllDefaultInputs()

        initialValues.forEachIndexed { index, amount ->
            if (index < inputEditTextList.size) {
                defaultInputRowList[index].visibility = View.VISIBLE
                inputEditTextList[index].setText(amount.toString())
            } else {
                addInputRow(initialAmount = amount)
            }
        }

        updateTotal()
    }

    private fun showDefaultEmptyInputs() {
        defaultInputRowList.forEach { row ->
            row.visibility = View.VISIBLE
        }

        inputEditTextList.forEach { editText ->
            editText.setText("")
        }
    }

    private fun hideAllDefaultInputs() {
        defaultInputRowList.forEach { row ->
            row.visibility = View.GONE
        }

        inputEditTextList.forEach { editText ->
            editText.setText("")
        }
    }

    private fun initListener() {
        closeIv.setOnClickListener {
            dismiss()
        }

        addToggleLl.setOnClickListener {
            addInputRow()
        }

        saveBtn.setOnClickListener {
            val values = getInputValues()

            if (values.isEmpty()) {
                Toast.makeText(context, "음수량을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = values.sum()
            onSaveClick?.invoke(total, values)
        }
    }

    private fun addInputRow(initialAmount: Int? = null) {
        val hiddenDefaultIndex = defaultInputRowList.indexOfFirst { row ->
            row.visibility == View.GONE
        }

        if (hiddenDefaultIndex != -1) {
            val targetRow = defaultInputRowList[hiddenDefaultIndex]
            val targetEditText = inputEditTextList[hiddenDefaultIndex]

            targetRow.visibility = View.VISIBLE
            targetEditText.setText(initialAmount?.toString() ?: "")

            if (initialAmount == null) {
                targetEditText.requestFocus()
                showKeyboard(targetEditText)
            }

            updateTotal()
            return
        }

        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.item_water_input, inputContainerLl, false)

        val inputEt = itemView.findViewById<EditText>(R.id.input_et)

        inputEt.hint = "100"
        inputEt.setText(initialAmount?.toString() ?: "")

        addTextWatcher(inputEt)
        inputEditTextList.add(inputEt)
        inputContainerLl.addView(itemView)

        if (initialAmount == null) {
            inputEt.requestFocus()
            showKeyboard(inputEt)
        }

        updateTotal()
    }

    private fun addTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
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
            ) = Unit

            override fun afterTextChanged(s: Editable?) {
                updateTotal()
            }
        })
    }

    private fun updateTotal() {
        val total = getInputValues().sum()
        totalTv.text = total.toString()
    }

    private fun getInputValues(): List<Int> {
        return inputEditTextList.mapNotNull { editText ->
            val row = editText.parent as? View

            if (row?.visibility == View.GONE) {
                return@mapNotNull null
            }

            val value = editText.text.toString().trim()

            if (value.isBlank()) {
                null
            } else {
                value.toIntOrNull()
            }
        }
    }

    private fun showKeyboard(editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}