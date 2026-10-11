package com.example

import com.example.data.parser.M3uParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testM3uParserWithMelbourneStream() {
    val sampleM3u = """
      #EXTM3U x-tvg-url="https://i.mjh.nz/au/Melbourne/epg.xml.gz"
      #EXTINF:-1 channel-id="mjh-seven-mel" tvg-id="mjh-seven-mel" tvg-logo="https://i.mjh.nz/.images/auto-seven-mel-9608a4.png" tvg-chno="71" group-title="Melbourne" , Seven
      #EXTVLCOPT:http-user-agent=otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client) libcurl/7.58.0 OpenSSL/1.0.2o zlib/1.2.11 clib/1.8.56
      #EXTVLCOPT:http-referrer=https://i.mjh.nz/.r/seven-mel.m3u8
      https://i.mjh.nz/.r/seven-mel.m3u8
      #EXTINF:-1 channel-id="mjh-7two-mel" tvg-id="mjh-7two-mel" tvg-logo="https://i.mjh.nz/.images/auto-7two-mel-701123.png" tvg-chno="72" group-title="Melbourne" , 7two
      https://i.mjh.nz/.r/7two-mel.m3u8
    """.trimIndent()

    val channels = M3uParser.parse(sampleM3u)
    assertEquals(2, channels.size)

    val ch1 = channels[0]
    assertEquals("mjh-seven-mel", ch1.id)
    assertEquals("Seven", ch1.name)
    assertEquals("71", ch1.channelNumber)
    assertEquals("Melbourne", ch1.group)
    assertEquals("https://i.mjh.nz/.r/seven-mel.m3u8", ch1.streamUrl)
    assertNotNull(ch1.logoUrl)
    assertNotNull(ch1.userAgent)

    val ch2 = channels[1]
    assertEquals("7two", ch2.name)
    assertEquals("72", ch2.channelNumber)
  }
}
