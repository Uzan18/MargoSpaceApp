package com.pemmob.margocoffe.data.repository

import com.pemmob.margocoffe.data.Coffee
import com.pemmob.margocoffe.data.dto.toDomain
import com.pemmob.margocoffe.data.remote.MenuApiService

class MenuRepository(
    private val api: MenuApiService
) {

    suspend fun getMenu(): Result<List<Coffee>> {
        return runCatching {
            api.getMenu().map { it.toDomain() }
        }
    }
}