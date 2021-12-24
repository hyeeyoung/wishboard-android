package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val wishRepository: WishRepository,
): ViewModel() {
    private var userNickName = MutableLiveData<String?>()
    private var userEmail = MutableLiveData<String?>()

    init {
        fetchUserInfo()
    }

    private fun fetchUserInfo() {
        // TODO nickname, profile_img fetch 필요,
        // 닉네임이 없는 유저는 이메일 hashcode에서 앞부분 6자리로 임시 부여
        userNickName.value = prefs?.getUserEmail().hashCode().toString().substring(0, 6)
        userEmail.value = prefs?.getUserEmail()
    }

    fun signOut() {
        prefs?.clearUserInfo()
    }

    fun getUserNickname(): LiveData<String?> = userNickName
    fun getUserEmail(): LiveData<String?> = userEmail

    companion object {
        private val TAG = "WishViewModel"
    }
}