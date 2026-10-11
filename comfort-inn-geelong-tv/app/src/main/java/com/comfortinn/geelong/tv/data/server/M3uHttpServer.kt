package com.comfortinn.geelong.tv.data.server

import android.content.Context
import android.util.Log
import com.comfortinn.geelong.tv.data.repository.ChannelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder

class M3uHttpServer(private val context: Context) {
    private val TAG = "M3uHttpServer"
    private var serverSocket: ServerSocket? = null
    private var isRunning = false
    private val repo = ChannelRepository(context)

    fun start(port: Int = 8080) {
        if (isRunning) return
        isRunning = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                serverSocket = ServerSocket(port)
                Log.i(TAG, "M3U Upload Server running on port $port")
                while (isRunning) {
                    val client = serverSocket?.accept() ?: break
                    handleClient(client)
                }
            } catch (e: Exception) {
                if (isRunning) Log.e(TAG, "Server error", e)
            }
        }
    }

    fun stop() {
        isRunning = false
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            // Ignored
        }
    }

    private fun handleClient(socket: Socket) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val output: OutputStream = socket.getOutputStream()

                val firstLine = reader.readLine() ?: return@launch
                val parts = firstLine.split(" ")
                val method = parts.getOrNull(0) ?: "GET"

                var contentLength = 0
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line!!.isEmpty()) break
                    if (line!!.startsWith("Content-Length:", ignoreCase = true)) {
                        contentLength = line!!.substring(15).trim().toIntOrNull() ?: 0
                    }
                }

                if (method == "POST" && contentLength > 0) {
                    val bodyChars = CharArray(contentLength)
                    reader.read(bodyChars, 0, contentLength)
                    val body = String(bodyChars)
                    
                    var newUrl: String? = null
                    val params = body.split("&")
                    for (param in params) {
                        val kv = param.split("=")
                        if (kv.size == 2 && kv[0] == "m3u_url") {
                            newUrl = URLDecoder.decode(kv[1], "UTF-8")
                        }
                    }

                    if (!newUrl.isNullOrBlank()) {
                        repo.setM3uUrl(newUrl)
                        repo.refreshChannelsAndEpg()
                    }

                    sendResponse(output, 200, "text/html", getSuccessHtml(newUrl ?: ""))
                } else {
                    sendResponse(output, 200, "text/html", getIndexHtml(repo.getM3uUrl()))
                }
                socket.close()
            } catch (e: Exception) {
                Log.e(TAG, "Client handle error", e)
            }
        }
    }

    private fun sendResponse(output: OutputStream, code: Int, contentType: String, content: String) {
        val bytes = content.toByteArray(Charsets.UTF_8)
        val header = "HTTP/1.1 $code OK\r\n" +
                "Content-Type: $contentType; charset=utf-8\r\n" +
                "Content-Length: ${bytes.size}\r\n" +
                "Connection: close\r\n\r\n"
        output.write(header.toByteArray(Charsets.UTF_8))
        output.write(bytes)
        output.flush()
    }

    private fun getIndexHtml(currentUrl: String): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>Comfort Inn Geelong - IPTV M3U Link Manager</title>
            <style>
                body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0A192F; color: #F8FAFC; padding: 24px; }
                .container { max-width: 600px; margin: 0 auto; background: #112240; border-radius: 16px; padding: 32px; border: 1px solid #334155; }
                h1 { color: #D4AF37; font-size: 22px; margin-top: 0; }
                p { color: #94A3B8; font-size: 14px; line-height: 1.5; }
                label { display: block; margin-top: 20px; font-weight: bold; color: #F8FAFC; }
                input[type=text] { width: 100%; box-sizing: border-box; padding: 12px; margin-top: 8px; border-radius: 8px; border: 1px solid #334155; background: #0A192F; color: #F8FAFC; font-size: 14px; }
                button { background: #D4AF37; color: #0A192F; font-weight: bold; padding: 14px 24px; border: none; border-radius: 8px; cursor: pointer; margin-top: 24px; font-size: 16px; width: 100%; }
                .badge { display: inline-block; background: #00B050; color: white; padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: bold; }
                .footer { margin-top: 24px; font-size: 12px; color: #64748B; text-align: center; }
            </style>
        </head>
        <body>
            <div class="container">
                <span class="badge">HOSPITALITY TV PORTAL</span>
                <h1>IPTV M3U Playlist Link Upload</h1>
                <p>Enter or paste any custom M3U/M3U8 live stream link to update the television channels instantly.</p>
                <form method="POST">
                    <label for="m3u_url">M3U Playlist URL:</label>
                    <input type="text" id="m3u_url" name="m3u_url" value="__URL_PLACEHOLDER__" required>
                    <button type="submit">Update TV Channels Now</button>
                </form>
                <div class="footer">Comfort Inn Geelong • 151 Ryrie St, Geelong VIC 3220</div>
            </div>
        </body>
        </html>
        """.trimIndent().replace("__URL_PLACEHOLDER__", currentUrl)
    }

    private fun getSuccessHtml(savedUrl: String): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>M3U Link Updated</title>
            <style>
                body { font-family: sans-serif; background: #0A192F; color: #F8FAFC; text-align: center; padding: 40px; }
                .box { max-width: 500px; margin: 0 auto; background: #112240; padding: 32px; border-radius: 16px; border: 1px solid #00B050; }
                h1 { color: #00B050; font-size: 24px; }
                p { color: #94A3B8; font-size: 15px; word-break: break-all; }
                a { display: inline-block; margin-top: 20px; color: #D4AF37; text-decoration: none; font-weight: bold; }
            </style>
        </head>
        <body>
            <div class="box">
                <h1>✓ M3U Playlist Updated!</h1>
                <p>The TV has received the new M3U link and is reloading the channels now:</p>
                <p><strong>__SAVED_URL__</strong></p>
                <a href="/">← Go Back</a>
            </div>
        </body>
        </html>
        """.trimIndent().replace("__SAVED_URL__", savedUrl)
    }

    fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        return addr.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "127.0.0.1"
    }
}
