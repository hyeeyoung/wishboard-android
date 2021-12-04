package com.hyeeyoung.wishboard.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.repository.sign.SignRepository
import com.hyeeyoung.wishboard.util.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignViewModel @Inject constructor(
    private val signRepository: SignRepository,
): ViewModel() {
    private var email = MutableLiveData<String>()
    private var password = MutableLiveData<String>()
    private var isValidEmailFormat = MutableLiveData(false)
    private var isValidPasswordFormat = MutableLiveData(false)
    private var isCompletedSignUp = MutableLiveData(false)

    fun onEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int){
        email.value = s.toString().trim()
        checkEmailFormatValidation()
    }

    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int){
        password.value = s.toString().trim()
        checkPasswordFormatValidation()
    }

    private fun checkEmailFormatValidation() {
        val emailPattern = Patterns.EMAIL_ADDRESS
        isValidEmailFormat.value = emailPattern.matcher(email.value).matches()
    }

    private fun checkPasswordFormatValidation() {
        val passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{7,15}.$")
        isValidPasswordFormat.value = passwordPattern.matcher(password.value).matches()
    }

    fun signUp() {
        viewModelScope.launch {
            safeLet(email.value, password.value) { email, password ->
                isCompletedSignUp.value = signRepository.signUp(email, password)
            }
        }
    }

    fun getUserEmail(): LiveData<String> = email
    fun getUserPassword(): LiveData<String> = password
    fun getValidEmailFormat(): LiveData<Boolean> = isValidEmailFormat
    fun getValidPasswordFormat(): LiveData<Boolean> = isValidPasswordFormat
    fun getCompletedSignUp(): LiveData<Boolean> = isCompletedSignUp

    companion object {
        private val TAG = "SignViewModel"
    }
}