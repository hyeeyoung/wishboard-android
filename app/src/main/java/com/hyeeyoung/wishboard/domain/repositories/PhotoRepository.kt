package com.hyeeyoung.wishboard.domain.repositories

import android.net.Uri

interface PhotoRepository {
    fun createCameraImageUri(): Uri? // TODO remove android dependency
}