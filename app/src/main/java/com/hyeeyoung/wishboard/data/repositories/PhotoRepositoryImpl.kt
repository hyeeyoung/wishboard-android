package com.hyeeyoung.wishboard.data.repositories

import android.app.Application
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.repositories.PhotoRepository
import com.hyeeyoung.wishboard.util.makePhotoFileName
import java.io.File
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val application: Application,
    private val localStorage: WishBoardPreference
) : PhotoRepository {
    override fun createCameraImageUri(): Uri? {
        val file = File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "wishtem")
        if (!file.exists()) file.mkdirs()

        val fileName = makePhotoFileName(localStorage.accessToken)
        val imageFile = File(file.absoluteFile, fileName)

        return FileProvider.getUriForFile(
            application.applicationContext,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            imageFile
        )
    }
}
