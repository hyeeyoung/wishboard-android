package com.hyeeyoung.wishboard.data.services

import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.storage.StorageException
import timber.log.Timber
import java.io.File

class AWSS3Service {
    suspend fun uploadFile(fileName: String, file: File): Boolean {
        val upload = Amplify.Storage.uploadFile(fileName, file)
        return try {
            val result = upload.result()
            Timber.d("Successfully uploaded: ${result.key}")
            true
        } catch (error: StorageException) {
            Timber.e("Upload failed", error)
            false
        }
    }

    suspend fun getImageUrl(fileName: String): String? {
        return try {
            val url = Amplify.Storage.getUrl(fileName).url.toString()
            Timber.d("Successfully generated: $url")
            url
        } catch (error: StorageException) {
            Timber.e("URL generation failure", error)
            null
        }
    }

    suspend fun removeImageUrl(fileName: String) {
        try {
            val result = Amplify.Storage.remove(fileName)
            Timber.d("Successfully removed ${result.key}")
        } catch (error: StorageException) {
            Timber.e("Remove failure", error)
        }
    }
}