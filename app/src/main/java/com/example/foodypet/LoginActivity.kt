package com.example.foodypet

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.foodypet.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPasswordToggle()

        binding.loginSignupBtn.setOnClickListener {
            val Intent = Intent(this, SignupActivity::class.java)
            startActivity(Intent)
        }

        // TODO : 로그인 API 연결
        binding.loginBtn.setOnClickListener {
            // TODO : accessToken 및 refreshToken Intent에 담아서 전달
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordToggle() {
        binding.loginPwd.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                val endDrawable = binding.loginPwd.compoundDrawables[drawableEnd]

                if (endDrawable != null) {
                    val drawableWidth = endDrawable.bounds.width()

                    if (event.rawX >= (binding.loginPwd.right - drawableWidth - binding.loginPwd.paddingEnd)) {
                        isPasswordVisible = !isPasswordVisible

                        if (isPasswordVisible) {
                            binding.loginPwd.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            binding.loginPwd.setCompoundDrawablesWithIntrinsicBounds(
                                0, 0, R.drawable.icon_eye_open, 0
                            )
                        } else {
                            binding.loginPwd.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            binding.loginPwd.setCompoundDrawablesWithIntrinsicBounds(
                                0, 0, R.drawable.icon_eye_close, 0
                            )
                        }

                        binding.loginPwd.setSelection(binding.loginPwd.text?.length ?: 0)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }
}