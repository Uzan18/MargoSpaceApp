package com.pemmob.margocoffe.data.remote

import com.pemmob.margocoffe.data.dto.MenuDto
import retrofit2.http.GET

interface MenuApiService {

    @GET("menu")
    suspend fun getMenu(): List<MenuDto>
}