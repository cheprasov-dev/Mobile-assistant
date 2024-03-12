package com.assistant

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

class TelegramAdapter(private val token: String, private val chatId: String) {

    fun sendMessage(message: String) {
        Thread {
            try {
                val encodedMessage = java.net.URLEncoder.encode(message, "UTF-8")
                val urlString = "https://api.telegram.org/bot$token/sendMessage?chat_id=$chatId&text=$encodedMessage"
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                val responseCode = urlConnection.responseCode
                if (responseCode == 200) {
                    Log.d("TelegramAdapter", "Message sent successfully, response code: $responseCode")
                } else {
                    Log.e("TelegramAdapter", "HTTP error while sending message, response code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("TelegramAdapter", "Error sending message to Telegram", e)
            }
        }.start()
    }
}