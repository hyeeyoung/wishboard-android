package com.hyeeyoung.wishboard.presentation.my

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.repositories.NotiRepository
import com.hyeeyoung.wishboard.domain.repositories.SignRepository
import com.hyeeyoung.wishboard.domain.repositories.UserRepository
import com.hyeeyoung.wishboard.util.ContentUriRequestBody
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.extension.toPlainNullableRequestBody
import com.hyeeyoung.wishboard.util.extension.toStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val application: Application,
    private val notiRepository: NotiRepository,
    private val signRepository: SignRepository,
    private val userRepository: UserRepository,
    private val localStorage: WishBoardPreference
) : ViewModel() {
    private var userEmail = MutableLiveData<String?>()
    private var userNickname = MutableLiveData<String>()

    val inputUserNickName = MutableStateFlow<String?>(null)
    private var inputUserEmail = MutableLiveData<String?>()
    private val _userProfileImageUri = MutableStateFlow<String?>(null)
    val userProfileImageUri get() = _userProfileImageUri.asStateFlow()
    private val _selectedProfileImageUri = MutableStateFlow<Uri?>(null)
    val selectedProfileImageUri get() = _selectedProfileImageUri.asStateFlow()

    private var pushState = MutableLiveData<Boolean?>()

    private val _userInfoUpdateState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val userInfoUpdateState get() = _userInfoUpdateState.asStateFlow()
    private val _isCompleteLogout = MutableLiveData<Boolean?>()
    val isCompleteLogout: LiveData<Boolean?> get() = _isCompleteLogout
    private var isCompleteUserDelete = MutableLiveData<Boolean?>()
    private var isExistNickname = MutableLiveData<Boolean?>()
    private var isCorrectedEmail = MutableLiveData<Boolean?>()
    val isValidNicknameFormat = inputUserNickName.map { nickname ->
        inputUserNickName.value = nickname?.trim()
        isExistNickname.value = null
        nickname?.matches(NICKNAME_PATTERN.toRegex()) == true && nickname.isNotEmpty()
    }.toStateFlow(viewModelScope, null)

    val newPassword = MutableStateFlow("")
    val reNewPassword = MutableStateFlow("")
    private val _passwordChangeState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val passwordChangeState: StateFlow<UiState<Boolean>> get() = _passwordChangeState
    val isValidPassword = newPassword.map {
        it.matches(PASSWORD_PATTERN.toRegex())
    }.toStateFlow(viewModelScope, false)
    val isEnabledPasswordCompleteButton =
        combine(isValidPassword, newPassword, reNewPassword) { isValid, new, renew ->
            isValid && (new == renew)
        }.toStateFlow(viewModelScope, false)

    val isEnabledEditCompleteButton = combine(
        inputUserNickName,
        isValidNicknameFormat,
        selectedProfileImageUri
    ) { nickname, isValidNickname, profileUri ->
        isValidNickname != false && !(nickname == userNickname.value && profileUri == null)
    }.toStateFlow(viewModelScope, false)

    fun fetchUserInfo() {
        viewModelScope.launch {
            userRepository.fetchUserInfo().let {
                userEmail.value = it?.email ?: localStorage.userEmail
                userNickname.value = it?.nickname ?: localStorage.userNickname
                _userProfileImageUri.value = it?.profileImage
                pushState.value = convertIntToBooleanPushState(it?.pushState)
            }
        }
    }

    fun updateUserInfo() {
        viewModelScope.launch {
            _userInfoUpdateState.value = UiState.Loading
            val nickname =
                if (userNickname.value == inputUserNickName.value) null else inputUserNickName.value
            val imageMultipartBody: MultipartBody.Part? =
                selectedProfileImageUri.value?.let { imageUri ->
                    ContentUriRequestBody(
                        application.baseContext,
                        "profile_img",
                        imageUri,
                    ).toFormData()
                }

            val result = userRepository.updateUserInfo(
                nickname.toPlainNullableRequestBody(),
                imageMultipartBody
            ).getOrNull()

            if (result?.first == true) {
                _userInfoUpdateState.value = UiState.Success(true)
                setUserInfo()
            } else {
                isExistNickname.value = result?.second == 409
                _userInfoUpdateState.value = UiState.Error(null)
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
            _isCompleteLogout.value = signRepository.logout().getOrNull() == true
        }
    }

    fun deleteUserAccount() {
        viewModelScope.launch {
            isCompleteUserDelete.value = userRepository.deleteUserAccount().getOrNull() == true
        }
    }

    fun changePassword() {
        viewModelScope.launch {
            _passwordChangeState.value = UiState.Loading
            userRepository.changePassword(newPassword.value)
                .onSuccess {
                    _passwordChangeState.value = UiState.Success(it)
                }.onFailure {
                    _passwordChangeState.value = UiState.Error(it.message)
                }
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

    fun onEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        inputUserEmail.value = s.toString().trim()
        isCorrectedEmail.value = null
    }

    fun resetUserInfo() {
        inputUserNickName.value = userNickname.value
        isExistNickname.value = null
//        _userProfileImageUri.value = null
        _selectedProfileImageUri.value = null
        _userInfoUpdateState.value = UiState.Empty
    }

    fun resetCorrectedEmail() {
        isCorrectedEmail.value = null
    }

    /** 유저 프로필 업데이트 이후 로컬에 닉네임을 저장 */
    // TODO 함수명 변경
    private fun setUserInfo() {
        localStorage.userNickname = inputUserNickName.value!!
    }

    fun setSelectedUserProfileImage(imageUri: Uri) {
        _selectedProfileImageUri.value = imageUri
    }

    fun checkCorrectedEmail(): Boolean {
        val isCorrected = inputUserEmail.value == userEmail.value
        isCorrectedEmail.value = isCorrected
        return isCorrected
    }

    fun getUserEmail(): LiveData<String?> = userEmail
    fun getUserNickname(): LiveData<String?> = userNickname
    fun getPushState(): LiveData<Boolean?> = pushState

    fun isExistNickname(): LiveData<Boolean?> = isExistNickname
    fun isCorrectedEmail(): LiveData<Boolean?> = isCorrectedEmail

    fun getCompleteDeleteUser(): LiveData<Boolean?> = isCompleteUserDelete

    companion object {
        private const val PASSWORD_PATTERN =
            "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[{!\"#\$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~/\\\\]).{7,15}.$"
        private const val NICKNAME_PATTERN = "^[가-힣ㄱ-ㅎa-zA-Z0-9]+\$"
    }
}