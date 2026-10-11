package com.comfortinn.geelong.tv.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getGeelongWeather(
        @Query("latitude") latitude: Double = -38.1499,
        @Query("longitude") longitude: Double = 144.3617,
        @Query("current") current: String = "temperature_2m,weather_code,relative_humidity_2m,wind_speed_10m",
        @Query("timezone") timezone: String = "Australia/Melbourne"
    ): OpenMeteoResponse
}
