package com.hyeeyoung.wishboard.viewmodel

import android.util.Patterns
import androidx.lifecycle.*
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
    private var registrationEmail = MutableLiveData<String>()
    private var registrationPassword = MutableLiveData<String>()
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

    init {
        initEnabledVerificationCodeButton()
    }

    fun onRegistrationEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        registrationEmail.value = s.toString().trim()
        isUnregisteredUser.value = null
        isCorrectedVerificationCode.value = null
        checkEmailFormatValidation()
    }

    fun onRegistrationPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        registrationPassword.value = s.toString().trim()
        checkPasswordFormatValidation()
    }

    fun onLoginEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        loginEmail.value = s.toString().trim()
    }

    fun onLoginPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        loginPassword.value = s.toString().trim()
    }

    fun onVerificationCodeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        isCorrectedVerificationCode.value = null
        inputVerificationCode.value = s.toString().trim()
    }

    private fun checkEmailFormatValidation() {
        val emailPattern = Patterns.EMAIL_ADDRESS
        isValidEmailFormat.value = emailPattern.matcher(registrationEmail.value).matches()
    }

    private fun checkPasswordFormatValidation() {
        val passwordPattern =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{7,15}.$")
        isValidPasswordFormat.value = passwordPattern.matcher(registrationPassword.value).matches()
    }

    fun signUp() {
        viewModelScope.launch {
            safeLet(registrationEmail.value, registrationPassword.value) { email, password ->
                isCompletedSignUp.value = signRepository.signUp(email, password)
            }
        }
    }

    fun signIn() {
        viewModelScope.launch {
            safeLet(loginEmail.value, loginPassword.value) { email, password ->
                isCompletedSignIn.value = signRepository.signIn(email, password)
            }
        }
    }

    fun signInEmail() {
        if (registrationEmail.value == null) return
        viewModelScope.launch {
            if (inputVerificationCode.value == verificationCode.value && registrationEmail.value != null) {
                isCompletedSignIn.value = signRepository.signInEmail(registrationEmail.value!!)
            } else {
                isCorrectedVerificationCode.value = false
            }
        }
    }

    fun requestVerificationMail() {
        verificationCode.value = null
        isCorrectedVerificationCode.value = null

        viewModelScope.launch {
            registrationEmail.value?.let { email ->
                val result = signRepository.requestVerificationMail(email)

                isUnregisteredUser.value = result.second == 404
                isCompletedSendMail.value = result.first.first
                result.first.second?.let { code -> verificationCode.value = code }
            }
        }
    }

    private fun initEnabledVerificationCodeButton() {
        isEnabledVerificationCodeButton.addSource(isUnregisteredUser) { isUnregistered ->
            combineEnabledVerificationCodeButton(isUnregistered, inputVerificationCode.value)
        }
        isEnabledVerificationCodeButton.addSource(inputVerificationCode) { code ->
            combineEnabledVerificationCodeButton(isUnregisteredUser.value, code)
        }
    }

    private fun combineEnabledVerificationCodeButton(isUnregisteredUser: Boolean?, verificationCode: String?) {
        val code = verificationCode?.trim()
        isEnabledVerificationCodeButton.value = isUnregisteredUser == false && !code.isNullOrEmpty()
    }

    fun getRegistrationEmail(): LiveData<String> = registrationEmail
    fun getRegistrationPassword(): LiveData<String> = registrationPassword
    fun getValidEmailFormat(): LiveData<Boolean> = isValidEmailFormat
    fun getValidPasswordFormat(): LiveData<Boolean> = isValidPasswordFormat
    fun getCompletedSignUp(): LiveData<Boolean> = isCompletedSignUp
    fun getCompletedSignIn(): LiveData<Boolean?> = isCompletedSignIn
    fun isUnregisteredUser(): LiveData<Boolean?> = isUnregisteredUser
    fun isCompletedSendMail(): LiveData<Boolean?> = isCompletedSendMail
    fun isCorrectedVerificationCode(): LiveData<Boolean?> = isCorrectedVerificationCode
    fun isEnabledVerificationCodeButton(): LiveData<Boolean> = isEnabledVerificationCodeButton

    companion object {
        private const val TAG = "SignViewModel"
    }
}