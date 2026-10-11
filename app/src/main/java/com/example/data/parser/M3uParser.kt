package com.example.data.parser

import com.example.data.model.Channel
import java.util.UUID

object M3uParser {

  private val TAG_REGEX = Regex("([a-zA-Z0-9_-]+)=\"([^\"]*)\"")

  fun parse(content: String, baseUrl: String = ""): List<Channel> {
    val channels = mutableListOf<Channel>()
    val lines = content.lines()

    var currentName = ""
    var currentLogo: String? = null
    var currentGroup = "Melbourne"
    var currentChannelNumber = ""
    var currentId = ""
    var currentUserAgent: String? = null
    var currentReferrer: String? = null
    var isInsideExtInf = false

    for (rawLine in lines) {
      val line = rawLine.trim()
      if (line.isEmpty()) continue

      if (line.startsWith("#EXTINF:", ignoreCase = true)) {
        isInsideExtInf = true
        currentUserAgent = null
        currentReferrer = null

        // Extract attributes
        val attributes = TAG_REGEX.findAll(line).associate { match ->
          match.groupValues[1].lowercase() to match.groupValues[2]
        }

        currentId = attributes["channel-id"]
          ?: attributes["tvg-id"]
          ?: attributes["id"]
          ?: UUID.randomUUID().toString()

        currentLogo = attributes["tvg-logo"] ?: attributes["logo"]
        currentGroup = attributes["group-title"] ?: attributes["group"] ?: "Melbourne"
        currentChannelNumber = attributes["tvg-chno"] ?: attributes["channel-number"] ?: ""

        // Extract Channel Title after the last comma
        val commaIndex = line.lastIndexOf(',')
        currentName = if (commaIndex != -1 && commaIndex < line.length - 1) {
          line.substring(commaIndex + 1).trim()
        } else {
          attributes["tvg-name"] ?: attributes["name"] ?: "Channel ${channels.size + 1}"
        }

        if (currentName.isEmpty()) {
          currentName = "Channel ${channels.size + 1}"
        }
      } else if (line.startsWith("#EXTVLCOPT:", ignoreCase = true)) {
        val opt = line.substringAfter(":").trim()
        if (opt.startsWith("http-user-agent=", ignoreCase = true)) {
          currentUserAgent = opt.substringAfter("=").trim()
        } else if (opt.startsWith("http-referrer=", ignoreCase = true)) {
          currentReferrer = opt.substringAfter("=").trim()
        }
      } else if (!line.startsWith("#")) {
        // Stream URL line
        var streamUrl = line
        if (!streamUrl.startsWith("http://") && !streamUrl.startsWith("https://") && baseUrl.isNotEmpty()) {
          streamUrl = if (baseUrl.endsWith("/")) {
            baseUrl + streamUrl
          } else {
            val lastSlash = baseUrl.lastIndexOf('/')
            if (lastSlash != -1) {
              baseUrl.substring(0, lastSlash + 1) + streamUrl
            } else {
              "$baseUrl/$streamUrl"
            }
          }
        }

        if (streamUrl.startsWith("http://") || streamUrl.startsWith("https://")) {
          val fallbackId = if (currentId.isNotEmpty()) currentId else "ch_${channels.size + 1}"
          val fallbackName = if (currentName.isNotEmpty()) currentName else "Channel ${channels.size + 1}"

          channels.add(
            Channel(
              id = fallbackId,
              name = fallbackName,
              streamUrl = streamUrl,
              logoUrl = currentLogo?.takeIf { it.isNotBlank() },
              channelNumber = currentChannelNumber.ifEmpty { (channels.size + 1).toString() },
              group = currentGroup.ifEmpty { "Melbourne" },
              userAgent = currentUserAgent,
              referrer = currentReferrer
            )
          )
        }

        // Reset state for next channel
        isInsideExtInf = false
        currentName = ""
        currentLogo = null
        currentGroup = "Melbourne"
        currentChannelNumber = ""
        currentId = ""
        currentUserAgent = null
        currentReferrer = null
      }
    }

    return channels
  }
}
