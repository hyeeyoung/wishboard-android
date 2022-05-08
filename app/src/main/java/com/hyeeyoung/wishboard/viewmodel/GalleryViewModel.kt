package com.hyeeyoung.wishboard.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.repository.common.GalleryPagingDataSource
import com.hyeeyoung.wishboard.repository.common.GalleryRepository
import com.hyeeyoung.wishboard.util.getTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val application: Application,
    private val galleryRepository: GalleryRepository,
) : ViewModel() {
    private val token = WishBoardApp.prefs.getUserToken()

    private val galleryImageUris = MutableLiveData<PagingData<Uri>>()
    private var selectedGalleryImageUri = MutableLiveData<Uri?>()
    private var imageFile: File? = null

    fun fetchGalleryImageUris(contentResolver: ContentResolver) {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = 10)) {
                GalleryPagingDataSource(contentResolver, galleryRepository)
            }.flow.cachedIn(viewModelScope)
                .collect { images ->
                    galleryImageUris.postValue(images)
                }
        }
    }

    fun createCameraImageFile(): File? {
        val file = File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "wishtem")
        if (!file.exists()) {
            file.mkdirs()
        }

        val fileName = makePhotoFileName()
        imageFile = File(file.absoluteFile, fileName)
        return imageFile
    }

    /** 갤러리 이미지를 내부저장소에 복사 */
    fun copyImageToInternalStorage(uri: Uri?): File? {
        if (uri == null) return null
        val fileName = makePhotoFileName()
        val file = File(application.cacheDir, fileName)

        try {
            val inputStream = application.contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                outputStream.write(buf, 0, len)
            }
            file.deleteOnExit()
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return file
    }

    /** 이미지 파일명 생성하는 함수로 해당 함수 호출 전 반드시 token null 체크해야함 */
    private fun makePhotoFileName(): String {
        val timestamp = getTimestamp()
        return ("${token!!.substring(7)}_${timestamp}.jpg")
    }

    fun setSelectedGalleryImageUri(imageUri: Uri?) {
        selectedGalleryImageUri.value = imageUri
    }

    fun getGalleryImageUris(): LiveData<PagingData<Uri>?> = galleryImageUris
    fun getSelectedGalleryImageUri(): LiveData<Uri?> = selectedGalleryImageUri
    fun getImageFile(): File? = imageFile

    companion object {
        private const val TAG = "GalleryViewModel"
    }
}