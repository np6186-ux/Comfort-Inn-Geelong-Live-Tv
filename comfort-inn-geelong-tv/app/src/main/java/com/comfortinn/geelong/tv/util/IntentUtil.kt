package com.comfortinn.geelong.tv.util

import android.content.Context
import android.content.Intent
import android.widget.Toast

data class StreamingApp(
    val name: String,
    val packageName: String,
    val primaryColor: Long,
    val initial: String
)

object IntentUtil {
    const val KAYO_PACKAGE = "au.com.streamotion.kayo.tv"

    val POPULAR_AU_APPS = listOf(
        StreamingApp("Kayo Sports", KAYO_PACKAGE, 0xFF00B050, "KAYO"),
        StreamingApp("YouTube", "com.google.android.youtube.tv", 0xFFFF0000, "YT"),
        StreamingApp("Netflix", "com.netflix.ninja", 0xFFE50914, "NETFLIX"),
        StreamingApp("Prime Video", "com.amazon.amazonvideo.livingroom", 0xFF00A8E1, "PRIME"),
        StreamingApp("Disney+", "com.disney.disneyplus", 0xFF113CCF, "DISNEY+"),
        StreamingApp("Stan", "au.com.stan.and", 0xFF0072CE, "STAN")
    )

    fun launchKayo(context: Context) {
        launchPackage(context, KAYO_PACKAGE, "Kayo Sports")
    }

    fun launchApp(context: Context, app: StreamingApp) {
        launchPackage(context, app.packageName, app.name)
    }

    private fun launchPackage(context: Context, packageName: String, displayName: String) {
        val pm = context.packageManager
        val leanbackIntent = pm.getLeanbackLaunchIntentForPackage(packageName)
        val defaultIntent = pm.getLaunchIntentForPackage(packageName)
        val targetIntent = leanbackIntent ?: defaultIntent

        if (targetIntent != null) {
            targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(targetIntent)
        } else {
            Toast.makeText(
                context,
                "$displayName is not installed on this TV. Please download from Google Play.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
