package com.hyeeyoung.wishboard.remote

import android.util.Log
import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.storage.StorageException
import java.io.File

class AWSS3Service {
    suspend fun uploadFile(fileName: String, file: File): Boolean {
        val upload = Amplify.Storage.uploadFile(fileName, file)
        return try {
            val result = upload.result()
            Log.d(TAG, "Successfully uploaded: ${result.key}")
            true
        } catch (error: StorageException) {
            Log.e(TAG, "Upload failed", error)
            false
        }
    }

    suspend fun getImageUrl(fileName: String): String? {
        return try {
            val url = Amplify.Storage.getUrl(fileName).url.toString()
            Log.d(TAG, "Successfully generated: $url")
            url
        } catch (error: StorageException) {
            Log.e(TAG, "URL generation failure", error)
            null
        }
    }

    companion object {
        private const val TAG = "AwsS3Service"
    }
}