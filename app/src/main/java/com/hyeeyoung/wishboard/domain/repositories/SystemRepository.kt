package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.domain.model.system.AppVersion

interface SystemRepository {
    suspend fun fetchAppVersion(): Result<AppVersion?>
}