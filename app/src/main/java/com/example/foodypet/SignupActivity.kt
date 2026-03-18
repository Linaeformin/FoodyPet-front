package com.example.foodypet

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.foodypet.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private var isIdAvailable = false
    private var isPasswordVisible = false
    private var isPasswordCheckVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setListeners()
        updateSignupButtonState()
    }

    private fun initView() {
        binding.signupIdCheckLl.visibility = android.view.View.GONE
        binding.signupPwdCheckLl.visibility = android.view.View.GONE
        binding.signupPwdCheckAvailableLl.visibility = android.view.View.GONE
    }

    private fun setListeners() {
        // 1. 아이디 입력 중 유효성 검사
        binding.signupId.addTextChangedListener {
            val id = it.toString().trim()

            // 아이디를 다시 수정하면 중복확인 상태 초기화
            isIdAvailable = false

            validateIdLength(id)
            updateSignupButtonState()
        }

        // 2. 중복 확인 버튼 클릭
        // TODO : 아이디 중복 확인 api 연결
        binding.btnDuplicateCheck.setOnClickListener {
            val id = binding.signupId.text.toString().trim()

            if (id.length < 7 || id.length > 15) {
                binding.signupIdCheckLl.visibility = android.view.View.VISIBLE
                binding.signupIdCheckIv.setImageResource(R.drawable.icon_x_circle)
                binding.signupIdCheckTv.text = "아이디는 7자 이상, 15자 이하여야 해요"
                isIdAvailable = false
                updateSignupButtonState()
                return@setOnClickListener
            }

            checkDuplicateIdFromServer(id)
        }

        // 3. 비밀번호 입력 중 유효성 검사
        binding.signupPwd.addTextChangedListener {
            validatePassword()
            validatePasswordMatch()
            updateSignupButtonState()
        }

        // 4. 비밀번호 확인 입력 중 일치 검사
        binding.signupPwdCheck.addTextChangedListener {
            validatePasswordMatch()
            updateSignupButtonState()
        }

        // 5. 비밀번호 보기/숨기기
        binding.iconPwdToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(
                isVisible = isPasswordVisible,
                editText = binding.signupPwd,
                eyeIcon = binding.iconPwdToggle
            )
        }

        binding.iconPwdCheckToggle.setOnClickListener {
            isPasswordCheckVisible = !isPasswordCheckVisible
            togglePasswordVisibility(
                isVisible = isPasswordCheckVisible,
                editText = binding.signupPwdCheck,
                eyeIcon = binding.iconPwdCheckToggle
            )
        }

        // 뒤로가기
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 회원가입 완료
        binding.signupBtn.setOnClickListener {
            if (!binding.signupBtn.isEnabled) return@setOnClickListener

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * 1. 아이디 길이 검사
     * 7자 이상, 15자 이하면 gone
     * 아니면 visible
     */
    private fun validateIdLength(id: String) {
        if (id.isEmpty()) {
            binding.signupIdCheckLl.visibility = android.view.View.GONE
            return
        }

        if (id.length in 7..15) {
            binding.signupIdCheckLl.visibility = android.view.View.GONE
        } else {
            binding.signupIdCheckLl.visibility = android.view.View.VISIBLE
            binding.signupIdCheckIv.setImageResource(R.drawable.icon_x_circle)
            binding.signupIdCheckTv.text = "아이디는 7자 이상, 15자 이하여야 해요"
        }
    }

    /**
     * 2. 서버 중복 확인
     * 지금은 예시로 조건문만 넣어둠
     * 나중에 서버 연결되면 여기서 API 응답값으로 분기하면 됨
     */
    private fun checkDuplicateIdFromServer(id: String) {
        // 예시:
        // true = 사용 가능
        // false = 사용 불가
        val isAvailableFromServer = id != "alreadyused"

        if (isAvailableFromServer) {
            isIdAvailable = true
            binding.signupIdCheckLl.visibility = android.view.View.VISIBLE
            binding.signupIdCheckIv.setImageResource(R.drawable.icon_check_circle) // 사용 가능한 아이콘으로 바꿔
            binding.signupIdCheckTv.text = "사용 가능한 아이디예요"
            binding.signupIdCheckTv.setTextColor(getColor(R.color.main))
        } else {
            isIdAvailable = false
            binding.signupIdCheckLl.visibility = android.view.View.VISIBLE
            binding.signupIdCheckIv.setImageResource(R.drawable.icon_x_circle)
            binding.signupIdCheckTv.text = "이미 사용 중인 아이디예요"
            binding.signupIdCheckTv.setTextColor(getColor(R.color.red))
        }

        updateSignupButtonState()
    }

    /**
     * 3. 비밀번호 검사
     * 영문 + 숫자 포함 && 7자 이상
     * 조건 만족 전까지 visible
     * 만족하면 gone
     */
    private fun validatePassword() {
        val password = binding.signupPwd.text.toString()

        if (password.isEmpty()) {
            binding.signupPwdCheckLl.visibility = android.view.View.GONE
            return
        }

        if (isValidPassword(password)) {
            binding.signupPwdCheckLl.visibility = android.view.View.GONE
        } else {
            binding.signupPwdCheckLl.visibility = android.view.View.VISIBLE
        }
    }

    /**
     * 4. 비밀번호 확인 일치 여부 검사
     * 일치하지 않으면 visible
     * 일치하면 gone
     */
    private fun validatePasswordMatch() {
        val password = binding.signupPwd.text.toString()
        val passwordCheck = binding.signupPwdCheck.text.toString()

        if (passwordCheck.isEmpty()) {
            binding.signupPwdCheckAvailableLl.visibility = android.view.View.GONE
            return
        }

        if (password == passwordCheck) {
            binding.signupPwdCheckAvailableLl.visibility = android.view.View.GONE
        } else {
            binding.signupPwdCheckAvailableLl.visibility = android.view.View.VISIBLE
        }
    }

    /**
     * 5. 비밀번호 가시성 조절
     */
    private fun togglePasswordVisibility(
        isVisible: Boolean,
        editText: android.widget.EditText,
        eyeIcon: android.widget.ImageView
    ) {
        if (isVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            eyeIcon.setImageResource(R.drawable.icon_eye_open)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            eyeIcon.setImageResource(R.drawable.icon_eye_close)
        }

        editText.setSelection(editText.text.length)
    }

    /**
     * 6. 회원가입 버튼 활성화 상태 반영
     * 사용 가능한 아이디 + 유효한 비밀번호 + 비밀번호 일치
     */
    private fun updateSignupButtonState() {
        val password = binding.signupPwd.text.toString()
        val passwordCheck = binding.signupPwdCheck.text.toString()

        val canSignup = isIdAvailable &&
                isValidPassword(password) &&
                password == passwordCheck &&
                passwordCheck.isNotEmpty()

        binding.signupBtn.isEnabled = canSignup
        binding.signupBtn.isClickable = canSignup

        if (canSignup) {
            binding.signupBtn.setBackgroundResource(R.drawable.bg_btn_login)
        } else {
            binding.signupBtn.setBackgroundResource(R.drawable.bg_signup_unavailable)
        }
    }

    /**
     * 영문 + 숫자 포함 && 7자 이상
     */
    private fun isValidPassword(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasMinLength = password.length >= 7

        return hasLetter && hasDigit && hasMinLength
    }
}