package com.hyeeyoung.wishboard.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.hyeeyoung.wishboard.remote.AWSS3Service
import com.hyeeyoung.wishboard.repository.noti.NotiRepository
import com.hyeeyoung.wishboard.repository.user.UserRepository
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val notiRepository: NotiRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var userEmail = MutableLiveData<String?>()
    private var userNickname = MutableLiveData<String?>()
    private var userProfileImage = MutableLiveData<String?>()

    private var inputUserNickName = MutableLiveData<String?>()
    private var userProfileImageUri = MutableLiveData<Uri?>()
    private var userProfileImageFile = MutableLiveData<File?>()

    private var isCompleteUpdateUserInfo = MutableLiveData<Boolean?>()
    private var isExistNickname = MutableLiveData<Boolean?>()
    private var isEnabledEditCompleteButton = MediatorLiveData<Boolean>()

    private val token = prefs?.getUserToken()

    init {
        initEnabledEditCompleteButton()
    }

    fun fetchUserInfo() {
        if (token == null) return
        viewModelScope.launch { // TODO 네트워크에 연결되어있지 않은 경우, 내부 저장소에서 유저 정보 가져오기
            userRepository.fetchUserInfo(token).let {
                userEmail.value = it?.email ?: prefs?.getUserEmail()
                userNickname.value = it?.nickname ?: prefs?.getUserNickName()
                userProfileImage.value = it?.profileImage
            }
        }
    }

    fun updateUserInfo() {
        if (token == null) return
        viewModelScope.launch {
            val nickname =
                if (userNickname.value == inputUserNickName.value) null else inputUserNickName.value

            // AWS 업로드
            val profile = userProfileImageFile.value
            profile?.let { file ->
                AWSS3Service().uploadFile(file.name, file)
            }

            // DB 업로드
            val result =
                userRepository.updateUserInfo(token, nickname, profile?.name)
            isCompleteUpdateUserInfo.value = result.first
            isExistNickname.value = result.second == 409

            if (isCompleteUpdateUserInfo.value == true) {
                setUserInfo()
            }
        }
    }

    fun updatePushNotiSettings(isChecked: Boolean) {
        if (token == null) return
        prefs?.setCheckedPushNoti(isChecked)
        viewModelScope.launch {
            notiRepository.updatePushNotiSettings(token, isChecked)
        }
    }

    fun signOut() {
        if (token == null) return
        viewModelScope.launch {
            userRepository.registerFCMToken(token,null)
        }
        prefs?.clearUserInfo()
    }

    fun onNicknameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        inputUserNickName.value = s.toString()
        isExistNickname.value = null
    }

    fun resetUserInfo() {
        inputUserNickName.value = userNickname.value
        isExistNickname.value = null
        userProfileImageUri.value = null
        isCompleteUpdateUserInfo.value = null
    }

    private fun setUserInfo() {
        prefs?.setUserNickName(inputUserNickName.value!!)
        userNickname.value = inputUserNickName.value
        userProfileImageFile.value?.let { file -> userProfileImage.value = file.name }
    }

    fun setSelectedUserProfileImage(imageUri: Uri, imageFile: File) {
        userProfileImageUri.value = imageUri
        userProfileImageFile.value = imageFile
    }

    private fun initEnabledEditCompleteButton() {
        isEnabledEditCompleteButton.addSource(inputUserNickName) { nickname ->
            combineEnabledEditCompleteButton(nickname, userProfileImageUri.value)
        }
        isEnabledEditCompleteButton.addSource(userProfileImageUri) { imageUri ->
            combineEnabledEditCompleteButton(inputUserNickName.value, imageUri)
        }
    }

    private fun combineEnabledEditCompleteButton(nickname: String?, imageUri: Uri?) {
        isEnabledEditCompleteButton.value =
            !(nickname == userNickname.value && imageUri == null || nickname.isNullOrBlank())
    }

    fun getUserEmail(): LiveData<String?> = userEmail
    fun getUserNickname(): LiveData<String?> = userNickname
    fun getUserProfileImage(): LiveData<String?> = userProfileImage

    fun getInputUserNickname(): LiveData<String?> = inputUserNickName
    fun getUserProfileImageUri(): LiveData<Uri?> = userProfileImageUri
    fun isExistNickname(): LiveData<Boolean?> = isExistNickname
    fun isEnabledEditCompleteButton(): LiveData<Boolean> = isEnabledEditCompleteButton
    fun getCompleteUpdateUserInfo(): LiveData<Boolean?> = isCompleteUpdateUserInfo

    companion object {
        private const val TAG = "MyViewModel"
    }
}