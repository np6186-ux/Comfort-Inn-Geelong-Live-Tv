# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Gson & OpenMeteo models
-keepclassmembers class com.comfortinn.geelong.tv.data.remote.** { <fields>; }
