package com.assistant

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue

class MessageQueue {
    private val queue = ConcurrentLinkedQueue<String>()

    fun enqueue(message: String) {
        queue.add(message)
    }

    fun dequeue(): String? = queue.poll()

    fun isEmpty(): Boolean = queue.isEmpty()
}

val messageQueue = MessageQueue()

class TelegramAdapter(private val token: String, private val chatId: String) {

    fun sendMessage(message: String) {
        Thread {
            try {
                sendInternal(message)
            } catch (e: Exception) {
                Log.e("TelegramAdapter", "Error sending message to Telegram $e", e)
                messageQueue.enqueue(message)
            }
        }.start()
    }

    private fun sendInternal(message: String) {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = currentDateTime.format(formatter)

        val modifiedMessage = "[Date: $date]\n$message"

        val encodedMessage = URLEncoder.encode(modifiedMessage, "UTF-8")
        val urlString = "https://api.telegram.org/bot$token/sendMessage?chat_id=$chatId&text=$encodedMessage"
        val url = URL(urlString)
        (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connect()

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("TelegramAdapter", "Message sent successfully, response code: $responseCode")
                sendQueuedMessages()
            } else {
                Log.e("TelegramAdapter", "HTTP error while sending message, response code: $responseCode")
                throw Exception("HTTP error, code: $responseCode")
            }
        }
    }

    private fun sendQueuedMessages() {
        val isEmptyMessageQueue = messageQueue.isEmpty()
        Log.d("TelegramAdapter.sendQueuedMessages", "check queue $isEmptyMessageQueue")

        while (!messageQueue.isEmpty()) {
            val message = messageQueue.dequeue()

            if (message != null) {
                try {
                    sendInternal(message)
                } catch (e: Exception) {
                    Log.e("TelegramAdapter", "Error sending queued message to Telegram", e)
                    break
                }
            }
        }
    }
}