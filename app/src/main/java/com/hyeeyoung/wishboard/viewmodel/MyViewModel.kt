package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.repository.noti.NotiRepository
import com.hyeeyoung.wishboard.repository.user.UserRepository
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val notiRepository: NotiRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var userNickName = MutableLiveData<String?>()
    private var inputUserNickName = MutableLiveData<String?>()
    private var userEmail = MutableLiveData<String?>()
    private var isCompleteUpdateUserInfo = MutableLiveData<Boolean?>()

    private val token = prefs?.getUserToken()

    init {
        fetchUserInfo()
    }

    private fun fetchUserInfo() {
        // TODO nickname, profile_img fetch 필요,
        // 닉네임이 없는 유저는 이메일 hashcode에서 앞부분 6자리로 임시 부여
        userNickName.value = prefs?.getUserEmail().hashCode().toString().substring(0, 6)
        userEmail.value = prefs?.getUserEmail()
    }

    fun updatePushNotiSettings(isChecked: Boolean) {
        if (token == null) return
        prefs?.setCheckedPushNoti(isChecked)
        viewModelScope.launch {
            notiRepository.updatePushNotiSettings(token, isChecked)
        }
    }

    fun updateUserNickname() {
        if (token == null || inputUserNickName.value == null) return
        viewModelScope.launch {
            // TODO 프로필 이미지와 닉네임 모두 변경 완료 시 isComplete 초기화
            isCompleteUpdateUserInfo.value = userRepository.updateUserNickname(token, inputUserNickName.value!!)
            if (isCompleteUpdateUserInfo.value == true) {
                userNickName.value = inputUserNickName.value
            }
        }
    }

    fun signOut() {
        prefs?.clearUserInfo()
    }

    fun onNicknameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        inputUserNickName.value = s.toString()
    }

    fun resetUserInfo() {
        isCompleteUpdateUserInfo.value = null
    }

    fun getUserNickname(): LiveData<String?> = userNickName
    fun getInputUserNickname(): LiveData<String?> = inputUserNickName
    fun getUserEmail(): LiveData<String?> = userEmail
    fun getCompleteUpdateUserInfo(): LiveData<Boolean?> = isCompleteUpdateUserInfo

    companion object {
        private val TAG = "WishViewModel"
    }
}