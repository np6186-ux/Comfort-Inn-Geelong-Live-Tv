package com.comfortinn.geelong.tv.util

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log

object DeviceNameUtil {
    private const val TAG = "DeviceNameUtil"

    fun getWelcomeGreeting(context: Context): String {
        val room = extractRoomNumber(context)
        return if (!room.isNullOrBlank()) {
            "Welcome to Room $room"
        } else {
            "Welcome to Comfort Inn Geelong"
        }
    }

    fun extractRoomNumber(context: Context): String? {
        val candidates = mutableListOf<String>()

        try {
            Settings.Global.getString(context.contentResolver, "device_name")?.let {
                if (it.isNotBlank()) candidates.add(it)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read Settings.Global.device_name", e)
        }

        try {
            Settings.Secure.getString(context.contentResolver, "bluetooth_name")?.let {
                if (it.isNotBlank()) candidates.add(it)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read Settings.Secure.bluetooth_name", e)
        }

        try {
            BluetoothAdapter.getDefaultAdapter()?.name?.let {
                if (it.isNotBlank()) candidates.add(it)
            }
        } catch (e: Exception) {
            // Ignored
        }

        if (Build.MODEL.isNotBlank()) {
            candidates.add(Build.MODEL)
        }

        for (candidate in candidates) {
            val room = parseRoomFromDeviceString(candidate)
            if (room != null) {
                return room
            }
        }
        return null
    }

    internal fun parseRoomFromDeviceString(input: String): String? {
        val cleaned = input.trim()

        val roomWordRegex = Regex("""(?i)(?:room|rm|unit|suite)\s*[:#\-]?\s*([A-Za-z0-9\-]+)""")
        roomWordRegex.find(cleaned)?.let {
            val num = it.groupValues[1].trim()
            if (num.isNotEmpty()) return num
        }

        val hotelPrefixRegex = Regex("""(?i)(?:CIG|GEE)[\s\-_]?(\d+[A-Za-z]?)""")
        hotelPrefixRegex.find(cleaned)?.let {
            val num = it.groupValues[1].trim()
            if (num.isNotEmpty()) return num
        }

        val trailingNumberRegex = Regex("""(?i)(?:tv|tcl)[\s\-_]+(\d{2,4}[A-Za-z]?)""")
        trailingNumberRegex.find(cleaned)?.let {
            val num = it.groupValues[1].trim()
            if (num.isNotEmpty()) return num
        }

        return null
    }
}
