package com.hyeeyoung.wishboard.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignViewModel: ViewModel() {
    private var email = MutableLiveData<String>()
    private var password = MutableLiveData<String>()
    private var isValidEmailFormat = MutableLiveData(false)

    fun onEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        email.value = s.toString().trim()
    }

    private fun checkEmailFormatValidation() {
        val emailPattern = Patterns.EMAIL_ADDRESS
        isValidEmailFormat.value = emailPattern.matcher(email.value).matches()
    }

    fun getUserEmail(): LiveData<String> = email
    fun getPassword(): LiveData<String> = password
    fun getValidEmailFormat(): LiveData<Boolean> = isValidEmailFormat
}