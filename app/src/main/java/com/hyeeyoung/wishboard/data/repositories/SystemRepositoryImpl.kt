package com.hyeeyoung.wishboard.data.repositories

import com.hyeeyoung.wishboard.data.services.retrofit.SystemService
import com.hyeeyoung.wishboard.domain.model.system.AppVersion
import com.hyeeyoung.wishboard.domain.repositories.SystemRepository
import javax.inject.Inject

class SystemRepositoryImpl @Inject constructor(private val systemService: SystemService) :
    SystemRepository {
    override suspend fun fetchAppVersion(): Result<AppVersion?> = runCatching {
        systemService.fetchAppVersion().body()?.data?.toDomain()
    }
}