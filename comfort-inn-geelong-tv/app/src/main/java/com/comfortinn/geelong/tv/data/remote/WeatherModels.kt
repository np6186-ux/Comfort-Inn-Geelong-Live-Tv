package com.comfortinn.geelong.tv.data.remote

import com.google.gson.annotations.SerializedName

data class OpenMeteoResponse(
    @SerializedName("current")
    val current: CurrentWeather?
)

data class CurrentWeather(
    @SerializedName("temperature_2m")
    val temperature: Double,
    @SerializedName("weather_code")
    val weatherCode: Int,
    @SerializedName("relative_humidity_2m")
    val humidity: Int?,
    @SerializedName("wind_speed_10m")
    val windSpeed: Double?
)

data class GeelongWeather(
    val temperatureC: Int,
    val conditionDescription: String,
    val weatherCode: Int
)
