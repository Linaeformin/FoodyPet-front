package com.example.foodypet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodypet.data.auth.LoginRequest
import com.example.foodypet.data.auth.TokenManager
import com.example.foodypet.databinding.ActivityLoginBinding
import com.example.foodypet.network.RetrofitClient
import kotlinx.coroutines.launch
import android.util.Log

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        setupPasswordToggle()

        binding.loginSignupBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val userId = binding.loginId.text.toString().trim()
        val password = binding.loginPwd.text.toString().trim()

        if (userId.isBlank()) {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isBlank()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.loginBtn.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.refreshApiService.login(
                    LoginRequest(
                        userId = userId,
                        password = password
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        tokenManager.saveTokens(
                            accessToken = body.accessToken,
                            refreshToken = body.refreshToken
                        )

                        Toast.makeText(
                            this@LoginActivity,
                            "로그인되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@LoginActivity, SplashActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "로그인 응답이 비어 있습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "아이디 또는 비밀번호를 확인해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("LOGIN_API", "로그인 API 연결 실패", e)

                Toast.makeText(
                    this@LoginActivity,
                    "서버와 연결할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.loginBtn.isEnabled = true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordToggle() {
        binding.loginPwd.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                val endDrawable = binding.loginPwd.compoundDrawables[drawableEnd]

                if (endDrawable != null) {
                    val drawableWidth = endDrawable.bounds.width()

                    if (event.rawX >= binding.loginPwd.right - drawableWidth - binding.loginPwd.paddingEnd) {
                        isPasswordVisible = !isPasswordVisible

                        if (isPasswordVisible) {
                            binding.loginPwd.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            binding.loginPwd.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.icon_eye_open,
                                0
                            )
                        } else {
                            binding.loginPwd.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            binding.loginPwd.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.icon_eye_close,
                                0
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