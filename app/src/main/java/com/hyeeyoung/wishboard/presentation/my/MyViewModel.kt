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
    private val _userInfoFetchState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val userInfoFetchState get() = _userInfoFetchState.asStateFlow()
    private var userEmail = MutableLiveData<String?>()
    private var _userNickname = MutableStateFlow("")
    val userNickname get() = _userNickname.asStateFlow()

    val inputNickName = MutableStateFlow<String?>(null)
    private val isExistNickname = MutableLiveData<Boolean?>()
    private val _userProfileImage = MutableStateFlow<String?>(null)
    val userProfileImage get() = _userProfileImage.asStateFlow()
    private val _selectedProfileImageUri = MutableStateFlow<Uri?>(null)
    val selectedProfileImageUri get() = _selectedProfileImageUri.asStateFlow()
    private val _userInfoUpdateState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val userInfoUpdateState get() = _userInfoUpdateState.asStateFlow()

    val isEnabledEditCompleteButton = combine(
        inputNickName,
        _userNickname,
        selectedProfileImageUri
    ) { inputNickname, userNickname, profileUri ->
        !inputNickname.isNullOrBlank() && !(inputNickname == userNickname && profileUri == null) // 닉네임이 공백문자로만 이루어져있지 않고, 닉네임과 프로필 이미지 모두 변경 사항이 없는 경우를 제외한 모든 경우에 완료 버튼을 활성화
    }.toStateFlow(viewModelScope, false)

    private var pushState = MutableLiveData<Boolean?>()

    private val _inputEmail = MutableLiveData<String?>()
    val inputEmail: LiveData<String?> get() = _inputEmail

    private val isCorrectedEmail = MutableLiveData<Boolean?>()
    private val _isCompleteLogout = MutableLiveData<Boolean?>()
    val isCompleteLogout: LiveData<Boolean?> get() = _isCompleteLogout
    private val isCompleteUserDelete = MutableLiveData<Boolean?>()

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

    fun fetchUserInfo() {
        viewModelScope.launch {
            userRepository.fetchUserInfo().let {
                _userInfoFetchState.value =
                    if (it == null) UiState.Error(null) else UiState.Success(true)
                userEmail.value = it?.email ?: localStorage.userEmail
                _userNickname.value = it?.nickname ?: localStorage.userNickname
                _userProfileImage.value = it?.profileImage
                pushState.value = convertIntToBooleanPushState(it?.pushState)
            }
        }
    }

    fun updateUserInfo() {
        viewModelScope.launch {
            _userInfoUpdateState.value = UiState.Loading

            inputNickName.value = inputNickName.value?.trim()
            val nickname =
                if (userNickname.value == inputNickName.value) null else inputNickName.value
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
                localStorage.userNickname = inputNickName.value!! // TODO need refactoring
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
        _inputEmail.value = s.toString().trim()
        isCorrectedEmail.value = null
    }

    fun resetUserInfo() {
        inputNickName.value = _userNickname.value
        isExistNickname.value = null
        _selectedProfileImageUri.value = null
        _userInfoUpdateState.value = UiState.Empty
    }

    fun resetCorrectedEmail() {
        isCorrectedEmail.value = null
    }

    fun setSelectedUserProfileImage(imageUri: Uri) {
        _selectedProfileImageUri.value = imageUri
    }

    fun checkCorrectedEmail(): Boolean {
        val isCorrected = _inputEmail.value == userEmail.value
        isCorrectedEmail.value = isCorrected
        return isCorrected
    }

    fun requestUserInfoFetchState() {
        _userInfoFetchState.value = UiState.Loading
    }

    fun resetExitEmail() {
        _inputEmail.value = null
    }

    fun getUserEmail(): LiveData<String?> = userEmail
    fun getPushState(): LiveData<Boolean?> = pushState

    fun isExistNickname(): LiveData<Boolean?> = isExistNickname
    fun isCorrectedEmail(): LiveData<Boolean?> = isCorrectedEmail

    fun getCompleteDeleteUser(): LiveData<Boolean?> = isCompleteUserDelete

    companion object {
        private const val PASSWORD_PATTERN =
            "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[{!\"#\$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~/\\\\]).{7,15}.$"
    }
}