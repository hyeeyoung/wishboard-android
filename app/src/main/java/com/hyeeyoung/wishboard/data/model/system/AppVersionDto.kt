package com.hyeeyoung.wishboard.data.model.system

import com.hyeeyoung.wishboard.domain.model.system.AppVersion

data class AppVersionDto(
    val platform: String,
    val minVersion: String,
    val recommendedVersion: String,
) {
    fun toDomain(): AppVersion = AppVersion(
        minVersionCode = minVersion.toIntOrNull() ?: 0,
        latestVersionCode = recommendedVersion.toIntOrNull() ?: 0
    )
}
