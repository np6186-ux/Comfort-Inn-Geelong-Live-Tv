package com.example.data.model

data class WeatherData(
  val temperature: Double = 19.0,
  val condition: String = "Partly Cloudy",
  val humidity: Int = 65,
  val windSpeed: Double = 15.0,
  val weatherCode: Int = 1,
  val location: String = "Geelong, VIC",
  val isDay: Boolean = true
) {
  fun getWeatherIconEmoji(): String = when (weatherCode) {
    0 -> if (isDay) "☀️" else "🌙" // Clear sky
    1, 2 -> if (isDay) "🌤️" else "☁️" // Mainly clear / partly cloudy
    3 -> "☁️" // Overcast
    45, 48 -> "🌫️" // Fog
    51, 53, 55 -> "🌦️" // Drizzle
    61, 63, 65 -> "🌧️" // Rain
    71, 73, 75 -> "🌨️" // Snow
    80, 81, 82 -> "🌧️" // Rain showers
    95, 96, 99 -> "⛈️" // Thunderstorm
    else -> "🌤️"
  }
}
