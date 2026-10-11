package com.comfortinn.geelong.tv.data.repository

import com.comfortinn.geelong.tv.data.remote.GeelongWeather
import com.comfortinn.geelong.tv.data.remote.WeatherApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class WeatherRepository {
    private val api: WeatherApi

    init {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(WeatherApi::class.java)
    }

    suspend fun fetchGeelongWeather(): GeelongWeather? {
        return try {
            val response = api.getGeelongWeather()
            val current = response.current ?: return null
            val desc = mapWeatherCodeToDesc(current.weatherCode)
            GeelongWeather(
                temperatureC = current.temperature.roundToInt(),
                conditionDescription = desc,
                weatherCode = current.weatherCode
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun mapWeatherCodeToDesc(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1, 2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Light Drizzle"
            61, 63, 65 -> "Rain Showers"
            71, 73, 75 -> "Snow Flurries"
            80, 81, 82 -> "Heavy Rain"
            95, 96, 99 -> "Thunderstorm"
            else -> "Fine"
        }
    }
}
