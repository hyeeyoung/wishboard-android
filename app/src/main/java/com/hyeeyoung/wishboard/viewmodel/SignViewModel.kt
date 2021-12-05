package com.hyeeyoung.wishboard.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.repository.sign.SignRepository
import com.hyeeyoung.wishboard.util.prefs
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
    private var isValidEmailFormat = MutableLiveData(false)
    private var isValidPasswordFormat = MutableLiveData(false)
    private var isCompletedSignUp = MutableLiveData(false)
    private var isCompletedSignIn = MutableLiveData<Boolean?>(null)

    fun onRegistrationEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        registrationEmail.value = s.toString().trim()
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

    fun signOut() {
        prefs?.clearUserInfo()
    }

    fun getRegistrationEmail(): LiveData<String> = registrationEmail
    fun getRegistrationPassword(): LiveData<String> = registrationPassword
    fun getValidEmailFormat(): LiveData<Boolean> = isValidEmailFormat
    fun getValidPasswordFormat(): LiveData<Boolean> = isValidPasswordFormat
    fun getCompletedSignUp(): LiveData<Boolean> = isCompletedSignUp
    fun getCompletedSignIn(): LiveData<Boolean?> = isCompletedSignIn

    companion object {
        private const val TAG = "SignViewModel"
    }
}