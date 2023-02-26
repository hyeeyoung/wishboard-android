package com.hyeeyoung.wishboard.presentation.my

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.repositories.NotiRepository
import com.hyeeyoung.wishboard.domain.repositories.UserRepository
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.util.ContentUriRequestBody
import com.hyeeyoung.wishboard.util.extension.addSourceList
import com.hyeeyoung.wishboard.util.extension.toPlainNullableRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val application: Application,
    private val notiRepository: NotiRepository,
    private val userRepository: UserRepository,
    private val localStorage: WishBoardPreference
) : ViewModel() {
    private var userEmail = MutableLiveData<String?>()
    private var userNickname = MutableLiveData<String>()
    private var userProfileImage = MutableLiveData<String?>()

    private var inputUserNickName = MutableLiveData<String>()
    private var inputUserEmail = MutableLiveData<String?>()
    private var userProfileImageUri = MutableLiveData<Uri?>()
    private var userProfileImageFile = MutableLiveData<File?>()

    private var pushState = MutableLiveData<Boolean?>()
    private var profileEditStatus = MutableLiveData<ProcessStatus>()

    private var isCompleteUpdateUserInfo = MutableLiveData<Boolean?>()
    private var isCompleteUserDelete = MutableLiveData<Boolean?>()
    private var isExistNickname = MutableLiveData<Boolean?>()
    private var isCorrectedEmail = MutableLiveData<Boolean?>()
    private var isValidNicknameFormat = MutableLiveData<Boolean>()

    val isEnabledEditCompleteButton = MediatorLiveData<Boolean>().apply {
        addSourceList(
            inputUserNickName,
            isValidNicknameFormat,
            userProfileImageUri
        ) {
            (isValidNicknameFormat.value == true && inputUserNickName.value != userNickname.value) || userProfileImageUri.value != null
        }
    }

    fun fetchUserInfo() {
        viewModelScope.launch {
            userRepository.fetchUserInfo().let {
                userEmail.value = it?.email ?: localStorage.userEmail
                userNickname.value = it?.nickname ?: localStorage.userNickname
                userProfileImage.value = it?.profileImage
                pushState.value = convertIntToBooleanPushState(it?.pushState)
            }
        }
    }

    fun updateUserInfo() {
        profileEditStatus.value = ProcessStatus.IN_PROGRESS
        viewModelScope.launch {
            val nickname =
                if (userNickname.value == inputUserNickName.value) null else inputUserNickName.value
            val imageMultipartBody: MultipartBody.Part? =
                userProfileImageUri.value?.let { imageUri ->
                    ContentUriRequestBody(
                        application.baseContext,
                        "profile_img",
                        imageUri,
                    ).toFormData()
                }

            val result =
                userRepository.updateUserInfo(
                    nickname.toString().toPlainNullableRequestBody(),
                    imageMultipartBody
                )
            isCompleteUpdateUserInfo.value = result?.first
            isExistNickname.value = result?.second == 409
            profileEditStatus.postValue(ProcessStatus.IDLE)

            if (isCompleteUpdateUserInfo.value == true) {
                setUserInfo()
            }
        }
    }

    fun updatePushState() {
        if (pushState.value == null) return
        pushState.value = !pushState.value!!
        viewModelScope.launch {
            notiRepository.updatePushState(pushState.value!!)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.registerFCMToken(null)
        }
        WishBoardApp.prefs.clearUserInfo()
    }

    fun deleteUserAccount() {
        viewModelScope.launch {
            isCompleteUserDelete.value = userRepository.deleteUserAccount().getOrNull() == true
        }
    }

    /** 서버에서 전달받은 push_state 값은 Int타입으로, 알림 스위치 on/off를 위해 Boolean타입으로 변경 */
    private fun convertIntToBooleanPushState(pushState: Int?): Boolean? {
        return when (pushState) {
            0 -> false
            1 -> true
            else -> false
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

    /** 유저 프로필 업데이트 이후 로컬에 닉네임을 저장 */
    // TODO 함수명 변경
    private fun setUserInfo() {
        localStorage.userNickname = inputUserNickName.value!!
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

    fun getUserEmail(): LiveData<String?> = userEmail
    fun getUserNickname(): LiveData<String?> = userNickname
    fun getUserProfileImage(): LiveData<String?> = userProfileImage
    fun getPushState(): LiveData<Boolean?> = pushState

    fun getInputUserNickname(): LiveData<String?> = inputUserNickName
    fun getUserProfileImageUri(): LiveData<Uri?> = userProfileImageUri

    fun isExistNickname(): LiveData<Boolean?> = isExistNickname
    fun isCorrectedEmail(): LiveData<Boolean?> = isCorrectedEmail

    fun getCompleteUpdateUserInfo(): LiveData<Boolean?> = isCompleteUpdateUserInfo
    fun getCompleteDeleteUser(): LiveData<Boolean?> = isCompleteUserDelete
    fun isValidNicknameFormat(): LiveData<Boolean?> = isValidNicknameFormat
    fun getProfileEditStatus(): LiveData<ProcessStatus> = profileEditStatus
}