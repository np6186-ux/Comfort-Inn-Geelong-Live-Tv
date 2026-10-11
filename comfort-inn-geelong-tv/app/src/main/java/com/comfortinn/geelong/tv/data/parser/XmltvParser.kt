package com.comfortinn.geelong.tv.data.parser

import android.util.Xml
import com.comfortinn.geelong.tv.data.local.ProgramEntity
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object XmltvParser {
    private val DATE_FORMAT = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }

    fun parse(inputStream: InputStream): List<ProgramEntity> {
        val programs = mutableListOf<ProgramEntity>()
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, "UTF-8")

        var eventType = parser.eventType
        var currentChannelId: String? = null
        var currentStartEpoch: Long = 0
        var currentEndEpoch: Long = 0
        var currentTitle: String? = null
        var currentDesc: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "programme" -> {
                            currentChannelId = parser.getAttributeValue(null, "channel")
                            val startStr = parser.getAttributeValue(null, "start")
                            val stopStr = parser.getAttributeValue(null, "stop")
                            currentStartEpoch = parseTime(startStr)
                            currentEndEpoch = parseTime(stopStr)
                            currentTitle = null
                            currentDesc = null
                        }
                        "title" -> {
                            currentTitle = parser.nextText()
                        }
                        "desc" -> {
                            currentDesc = parser.nextText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "programme" && currentChannelId != null && currentTitle != null) {
                        programs.add(
                            ProgramEntity(
                                channelTvgId = currentChannelId,
                                title = currentTitle,
                                description = currentDesc,
                                startTimeEpoch = currentStartEpoch,
                                endTimeEpoch = currentEndEpoch
                            )
                        )
                    }
                }
            }
            eventType = parser.next()
        }
        return programs
    }

    private fun parseTime(raw: String?): Long {
        if (raw.isNullOrBlank()) return 0
        return try {
            val date = DATE_FORMAT.parse(raw)
            date?.time ?: 0
        } catch (e: Exception) {
            0
        }
    }
}
