package com.hyeeyoung.wishboard.viewmodel

import android.os.CountDownTimer
import android.util.Patterns
import androidx.lifecycle.*
import com.hyeeyoung.wishboard.model.common.ProcessStatus
import com.hyeeyoung.wishboard.repository.sign.SignRepository
import com.hyeeyoung.wishboard.util.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignViewModel @Inject constructor(
    private val signRepository: SignRepository,
) : ViewModel() {
    private var loginEmail = MutableLiveData<String>()
    private var loginPassword = MutableLiveData<String>()
    private var registrationEmail = MutableLiveData<String?>()
    private var registrationPassword = MutableLiveData<String?>()
    private var verificationCode = MutableLiveData<String?>()
    private var inputVerificationCode = MutableLiveData<String>()

    private var isValidEmailFormat = MutableLiveData<Boolean>()
    private var isValidPasswordFormat = MutableLiveData(false)
    private var isUnregisteredUser = MutableLiveData<Boolean?>(null)
    private var isCorrectedVerificationCode = MutableLiveData<Boolean?>(null)
    private var isEnabledVerificationCodeButton = MediatorLiveData<Boolean>()

    private var isCompletedSignUp = MutableLiveData(false)
    private var isCompletedSignIn = MutableLiveData<Boolean?>(null)
    private var isCompletedSendMail = MutableLiveData<Boolean?>(null)

    private var signProcessStatus = MutableLiveData<ProcessStatus>()
    private var timerValue = MutableLiveData<String?>()

    private var timer = object : CountDownTimer(300000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val second = (millisUntilFinished / 1000)
            timerValue.value = "${second / 60}:${String.format("%02d", second % 60)}"
        }

        override fun onFinish() {}
    }

    init {
        initEnabledVerificationCodeButton()
    }

    fun signUp() {
        signProcessStatus.value = ProcessStatus.IN_PROGRESS
        viewModelScope.launch {
            safeLet(registrationEmail.value, registrationPassword.value) { email, password ->
                isCompletedSignUp.value = signRepository.signUp(email, password)
                signProcessStatus.postValue(ProcessStatus.IDLE)
            }
        }
    }

    fun signIn() {
        signProcessStatus.value = ProcessStatus.IN_PROGRESS
        viewModelScope.launch {
            safeLet(loginEmail.value, loginPassword.value) { email, password ->
                isCompletedSignIn.value = signRepository.signIn(email, password)
                signProcessStatus.postValue(ProcessStatus.IDLE)
            }
        }
    }

    fun signInEmail() {
        timer.onFinish()

        signProcessStatus.value = ProcessStatus.IN_PROGRESS
        if (loginEmail.value == null) return
        viewModelScope.launch {
            if (inputVerificationCode.value == verificationCode.value) {
                isCompletedSignIn.value = signRepository.signInEmail(loginEmail.value!!)
            } else {
                isCorrectedVerificationCode.value = false
            }
            signProcessStatus.postValue(ProcessStatus.IDLE)
        }
    }

    fun requestVerificationMail() {
        timer.cancel()
        timer.start()

        verificationCode.value = null
        isCorrectedVerificationCode.value = null

        viewModelScope.launch {
            loginEmail.value?.let { email ->
                val result = signRepository.requestVerificationMail(email)

                isUnregisteredUser.value = result?.second == 404
                isCompletedSendMail.value = result?.first?.first
                result?.first?.second?.let { code -> verificationCode.value = code }
            }
        }
    }

    fun checkRegisteredUser() {
        signProcessStatus.value = ProcessStatus.IN_PROGRESS
        viewModelScope.launch {
            registrationEmail.value?.let { email ->
                val result = signRepository.checkRegisteredUser(email)
                safeLet(result?.first, result?.second) { isSuccessful, resultCode ->
                    setRegisteredUser(isSuccessful, resultCode)
                }
                signProcessStatus.postValue(ProcessStatus.IDLE)
            }
        }
    }

    private fun setRegisteredUser(isSuccessful: Boolean, resultCode: Int) {
        isUnregisteredUser.value = when {
            isSuccessful -> {
                true
            }
            resultCode == 409 -> {
                false
            }
            else -> {
                null
            }
        }
    }

    fun onRegistrationEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        isUnregisteredUser.value = null
        registrationEmail.value = s.toString().trim()
        checkEmailFormatValidation(registrationEmail.value)
    }

    fun onRegistrationPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        registrationPassword.value = s.toString().trim()
        checkPasswordFormatValidation(registrationPassword.value)
    }

    fun onLoginEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // 이메일 입력값 변경 시 가입자 여부 및 인증 코드 일치 여부 값을 초기화
        isUnregisteredUser.value = null // TODO need refactoring
        isCorrectedVerificationCode.value = null
        loginEmail.value = s.toString().trim()
        checkEmailFormatValidation(loginEmail.value)
    }

    fun onLoginPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        loginPassword.value = s.toString().trim()
    }

    fun onVerificationCodeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // 인증코드 입력값 변경 시 인증코드 일치 여부 값을 초기화
        isCorrectedVerificationCode.value = null // TODO need refactoring
        inputVerificationCode.value = s.toString().trim()
    }

    private fun checkEmailFormatValidation(email: String?) {
        val emailPattern = Patterns.EMAIL_ADDRESS
        isValidEmailFormat.value = emailPattern.matcher(email).matches()
    }

    private fun checkPasswordFormatValidation(password: String?) {
        val passwordPattern =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{7,15}.$")
        isValidPasswordFormat.value = passwordPattern.matcher(password).matches()
    }

    private fun initEnabledVerificationCodeButton() {
        isEnabledVerificationCodeButton.addSource(isUnregisteredUser) { isUnregistered ->
            combineEnabledVerificationCodeButton(isUnregistered, inputVerificationCode.value)
        }
        isEnabledVerificationCodeButton.addSource(inputVerificationCode) { code ->
            combineEnabledVerificationCodeButton(isUnregisteredUser.value, code)
        }
    }

    private fun combineEnabledVerificationCodeButton(
        isUnregisteredUser: Boolean?,
        verificationCode: String?
    ) {
        val code = verificationCode?.trim()
        isEnabledVerificationCodeButton.value = isUnregisteredUser == false && !code.isNullOrEmpty()
    }

    fun resetRegistrationEmail() {
        isUnregisteredUser.value = null
        registrationEmail.value = null
    }

    fun resetRegistrationPassword() {
        isUnregisteredUser.value = null
        registrationPassword.value = null
    }

    fun terminateTimer() {
        timer.cancel()
    }

    fun getLoginEmail(): LiveData<String> = loginEmail
    fun getLoginPassword(): LiveData<String> = loginPassword
    fun getRegistrationEmail(): LiveData<String?> = registrationEmail
    fun getRegistrationPassword(): LiveData<String?> = registrationPassword
    fun getTimer(): LiveData<String?> = timerValue

    fun getValidEmailFormat(): LiveData<Boolean> = isValidEmailFormat
    fun getValidPasswordFormat(): LiveData<Boolean> = isValidPasswordFormat
    fun getCompletedSignUp(): LiveData<Boolean> = isCompletedSignUp
    fun getCompletedSignIn(): LiveData<Boolean?> = isCompletedSignIn
    fun isUnregisteredUser(): LiveData<Boolean?> = isUnregisteredUser
    fun isCompletedSendMail(): LiveData<Boolean?> = isCompletedSendMail
    fun isCorrectedVerificationCode(): LiveData<Boolean?> = isCorrectedVerificationCode
    fun isEnabledVerificationCodeButton(): LiveData<Boolean> = isEnabledVerificationCodeButton

    fun getSignProcessStatus(): LiveData<ProcessStatus> = signProcessStatus

    companion object {
        private const val TAG = "SignViewModel"
    }
}