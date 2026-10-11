package com.example.data.repository

import com.example.data.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WeatherRepository {

  private val client = OkHttpClient.Builder()
    .connectTimeout(8, TimeUnit.SECONDS)
    .readTimeout(8, TimeUnit.SECONDS)
    .build()

  suspend fun fetchGeelongWeather(): Result<WeatherData> = withContext(Dispatchers.IO) {
    try {
      val url = "https://api.open-meteo.com/v1/forecast?latitude=-38.1499&longitude=144.3617&current=temperature_2m,relative_humidity_2m,is_day,weather_code,wind_speed_10m&timezone=Australia%2FSydney"
      val request = Request.Builder()
        .url(url)
        .header("User-Agent", "ComfortInnGeelongTV/1.0")
        .build()

      client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
          return@withContext Result.failure(Exception("Weather HTTP ${response.code}"))
        }

        val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty weather response"))
        val json = JSONObject(body)
        val current = json.getJSONObject("current")

        val temp = current.optDouble("temperature_2m", 19.0)
        val humidity = current.optInt("relative_humidity_2m", 65)
        val isDay = current.optInt("is_day", 1) == 1
        val code = current.optInt("weather_code", 1)
        val wind = current.optDouble("wind_speed_10m", 14.0)

        val condition = getWeatherDescription(code)

        Result.success(
          WeatherData(
            temperature = temp,
            condition = condition,
            humidity = humidity,
            windSpeed = wind,
            weatherCode = code,
            location = "Geelong, VIC",
            isDay = isDay
          )
        )
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  private fun getWeatherDescription(code: Int): String = when (code) {
    0 -> "Clear Sky"
    1 -> "Mainly Clear"
    2 -> "Partly Cloudy"
    3 -> "Overcast"
    45, 48 -> "Foggy"
    51, 53, 55 -> "Drizzle"
    61, 63, 65 -> "Rain"
    71, 73, 75 -> "Snow"
    80, 81, 82 -> "Rain Showers"
    95, 96, 99 -> "Thunderstorm"
    else -> "Fine & Mild"
  }
}
