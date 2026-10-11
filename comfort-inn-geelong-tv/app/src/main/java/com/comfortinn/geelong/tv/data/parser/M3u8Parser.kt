package com.comfortinn.geelong.tv.data.parser

import com.comfortinn.geelong.tv.data.local.ChannelEntity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object M3u8Parser {
    private val TVG_ID_REGEX = Regex("""tvg-id="([^"]*)"""")
    private val TVG_NAME_REGEX = Regex("""tvg-name="([^"]*)"""")
    private val TVG_LOGO_REGEX = Regex("""tvg-logo="([^"]*)"""")
    private val GROUP_TITLE_REGEX = Regex("""group-title="([^"]*)"""")
    private val CHANNEL_NO_REGEX = Regex("""tvg-chno="([^"]*)"""")

    fun parse(inputStream: InputStream): List<ChannelEntity> {
        val channels = mutableListOf<ChannelEntity>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        var currentTvgId: String? = null
        var currentTvgName: String? = null
        var currentLogo: String? = null
        var currentGroup: String? = null
        var currentChNo: Int? = null
        var autoNumber = 1

        while (reader.readLine().also { line = it } != null) {
            val trimmed = line!!.trim()
            if (trimmed.startsWith("#EXTINF:")) {
                currentTvgId = TVG_ID_REGEX.find(trimmed)?.groupValues?.get(1)
                currentTvgName = TVG_NAME_REGEX.find(trimmed)?.groupValues?.get(1)
                currentLogo = TVG_LOGO_REGEX.find(trimmed)?.groupValues?.get(1)
                currentGroup = GROUP_TITLE_REGEX.find(trimmed)?.groupValues?.get(1)
                currentChNo = CHANNEL_NO_REGEX.find(trimmed)?.groupValues?.get(1)?.toIntOrNull()

                val commaIndex = trimmed.lastIndexOf(',')
                if (commaIndex != -1 && (currentTvgName.isNullOrBlank() || currentTvgName == currentTvgId)) {
                    val rawName = trimmed.substring(commaIndex + 1).trim()
                    if (rawName.isNotBlank()) currentTvgName = rawName
                }
            } else if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                val streamUrl = trimmed
                val name = currentTvgName ?: "Channel $autoNumber"
                val tvgId = currentTvgId ?: "ch_$autoNumber"
                val chNo = currentChNo ?: autoNumber

                channels.add(
                    ChannelEntity(
                        tvgId = tvgId,
                        channelNumber = chNo,
                        name = name,
                        logoUrl = currentLogo,
                        streamUrl = streamUrl,
                        groupTitle = currentGroup
                    )
                )

                autoNumber++
                currentTvgId = null
                currentTvgName = null
                currentLogo = null
                currentGroup = null
                currentChNo = null
            }
        }
        return channels
    }
}
