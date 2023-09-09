package com.example.nasaimageapi.api

import com.example.nasaimageapi.models.PexelVideoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PexelsApiService {
    @GET("videos/popular")
    fun getPopularVideos(
        @Header("Authorization") authorization: String,
        @Query("per_page") perPage: Int
    ): Call<PexelVideoResponse> // Replace YourDataModel with the actual data model class
}
