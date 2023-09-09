package com.example.nasaimageapi.models

data class NasaImageApiResponse(
    val date: String?,
    val explanation: String?,
    val hdurl: String?,
    val media_type: String?,
    val service_version: String?,
    val title: String?,
    val url: String?


) {
    override fun toString(): String {
        return "NasaImageApiResponse(date=$date, explanation=$explanation, hdurl=$hdurl, media_type=$media_type, service_version=$service_version, title=$title, url=$url)"
    }
}