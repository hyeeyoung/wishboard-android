package com.hyeeyoung.wishboard.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.model.common.ProcessStatus
import com.hyeeyoung.wishboard.repository.noti.NotiRepository
import com.hyeeyoung.wishboard.repository.user.UserRepository
import com.hyeeyoung.wishboard.service.AWSS3Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Pattern
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
    private var inputUserEmail = MutableLiveData<String?>()
    private var userProfileImageUri = MutableLiveData<Uri?>()
    private var userProfileImageFile = MutableLiveData<File?>()

    private var isCompleteUpdateUserInfo = MutableLiveData<Boolean?>()
    private var isCompleteUserDelete = MutableLiveData<Boolean?>()
    private var isExistNickname = MutableLiveData<Boolean?>()
    private var isCorrectedEmail = MutableLiveData<Boolean?>()
    private var isEnabledEditCompleteButton = MediatorLiveData<Boolean>()
    private var isValidNicknameFormat = MutableLiveData<Boolean?>()
    private var pushState = MutableLiveData<Boolean?>()

    private var profileEditStatus = MutableLiveData<ProcessStatus>()

    private val token = WishBoardApp.prefs.getUserToken()

    init {
        initEnabledEditCompleteButton()
    }

    fun fetchUserInfo() {
        if (token == null) return
        viewModelScope.launch { // TODO 네트워크에 연결되어있지 않은 경우, 내부 저장소에서 유저 정보 가져오기
            userRepository.fetchUserInfo(token).let {
                userEmail.value = it?.email ?: WishBoardApp.prefs.getUserEmail()
                userNickname.value = it?.nickname ?: WishBoardApp.prefs.getUserNickName()
                userProfileImage.value = it?.profileImage
                pushState.value = convertIntToBooleanPushState(it?.pushState)
            }
        }
    }

    fun updateUserInfo() {
        profileEditStatus.value = ProcessStatus.IN_PROGRESS
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
            isCompleteUpdateUserInfo.value = result?.first
            isExistNickname.value = result?.second == 409

            profileEditStatus.postValue(ProcessStatus.IDLE)

            if (isCompleteUpdateUserInfo.value == true) {
                setUserInfo()
            }
        }
    }

    fun updatePushState() {
        if (token == null || pushState.value == null) return
        pushState.value = !pushState.value!!
        viewModelScope.launch {
            notiRepository.updatePushState(token, pushState.value!!)
        }
    }

    fun signOut() {
        if (token == null) return
        viewModelScope.launch {
            userRepository.registerFCMToken(token, null)
        }
        WishBoardApp.prefs.clearUserInfo()
    }

    fun deleteUserAccount() {
        if (token == null) return
        viewModelScope.launch {
            isCompleteUserDelete.postValue(userRepository.deleteUserAccount(token))
        }
        WishBoardApp.prefs.clearUserInfo()
    }

    /** 서버에서 전달받은 push_state 값은 Int타입으로, 알림 스위치 on/off를 위해 Boolean타입으로 변경 */
    private fun convertIntToBooleanPushState(pushState: Int?): Boolean? {
        return when (pushState) {
            0 -> false
            1 -> true
            else -> null
        }
    }

    private fun checkNicknameFormatValidation(nickname: String?) {
        val nicknamePattern =
            Pattern.compile("^[가-힣ㄱ-ㅎa-zA-Z0-9]+\$")
        isValidNicknameFormat.value =
            nicknamePattern.matcher(nickname).matches() && !nickname.isNullOrEmpty()
    }

    fun onNicknameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        inputUserNickName.value = s.toString().trim()
        checkNicknameFormatValidation(inputUserNickName.value)
        isExistNickname.value = null
    }

    fun onEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        inputUserEmail.value = s.toString().trim()
        isCorrectedEmail.value = null
    }

    fun resetUserInfo() {
        inputUserNickName.value = userNickname.value
        isExistNickname.value = null
        userProfileImageUri.value = null
        isCompleteUpdateUserInfo.value = null
    }

    fun resetCorrectedEmail() {
        isCorrectedEmail.value = null
    }

    private fun setUserInfo() {
        WishBoardApp.prefs.setUserNickName(inputUserNickName.value!!)
        userNickname.value = inputUserNickName.value
        userProfileImageFile.value?.let { file -> userProfileImage.value = file.name }
    }

    fun setSelectedUserProfileImage(imageUri: Uri, imageFile: File) {
        userProfileImageUri.value = imageUri
        userProfileImageFile.value = imageFile
    }

    fun checkCorrectedEmail(): Boolean {
        val isCorrected = inputUserEmail.value == userEmail.value
        isCorrectedEmail.value = isCorrected
        return isCorrected
    }

    private fun initEnabledEditCompleteButton() {
        isEnabledEditCompleteButton.addSource(inputUserNickName) { nickname ->
            combineEnabledEditCompleteButton(
                nickname,
                userProfileImageUri.value,
                isValidNicknameFormat.value
            )
        }
        isEnabledEditCompleteButton.addSource(userProfileImageUri) { imageUri ->
            combineEnabledEditCompleteButton(
                inputUserNickName.value,
                imageUri,
                isValidNicknameFormat.value
            )
        }
        isEnabledEditCompleteButton.addSource(isValidNicknameFormat) { isValid ->
            combineEnabledEditCompleteButton(
                inputUserNickName.value,
                userProfileImageUri.value,
                isValid
            )
        }
    }

    private fun combineEnabledEditCompleteButton(
        nickname: String?,
        imageUri: Uri?,
        isValidNickname: Boolean?
    ) {
        isEnabledEditCompleteButton.value =
            !(nickname == userNickname.value && imageUri == null) && isValidNickname == true
    }

    fun getUserEmail(): LiveData<String?> = userEmail
    fun getUserNickname(): LiveData<String?> = userNickname
    fun getUserProfileImage(): LiveData<String?> = userProfileImage
    fun getPushState(): LiveData<Boolean?> = pushState

    fun getInputUserNickname(): LiveData<String?> = inputUserNickName
    fun getUserProfileImageUri(): LiveData<Uri?> = userProfileImageUri

    fun isExistNickname(): LiveData<Boolean?> = isExistNickname
    fun isEnabledEditCompleteButton(): LiveData<Boolean> = isEnabledEditCompleteButton
    fun isCorrectedEmail(): LiveData<Boolean?> = isCorrectedEmail

    fun getCompleteUpdateUserInfo(): LiveData<Boolean?> = isCompleteUpdateUserInfo
    fun getCompleteDeleteUser(): LiveData<Boolean?> = isCompleteUserDelete
    fun isValidNicknameFormat(): LiveData<Boolean?> = isValidNicknameFormat

    fun getProfileEditStatus(): LiveData<ProcessStatus> = profileEditStatus

    companion object {
        private const val TAG = "MyViewModel"
    }
}