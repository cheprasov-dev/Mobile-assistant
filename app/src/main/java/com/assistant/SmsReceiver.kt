package com.assistant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Проверяем, что это действие для получения SMS
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle: Bundle? = intent.extras
            try {
                if (bundle != null) {
                    // Извлекаем данные из сообщения
                    val pdusObj = bundle["pdus"] as Array<*>
                    for (pdus in pdusObj.indices) {
                        val currentMessage: SmsMessage = SmsMessage.createFromPdu(pdusObj[pdus] as ByteArray, bundle.getString("format"))
                        val phoneNumber: String = currentMessage.displayOriginatingAddress
                        val message: String = currentMessage.displayMessageBody

                        Log.i("SmsReceiver", "Отправитель: $phoneNumber, Сообщение: $message")
                        // Здесь можно добавить логику для обработки SMS, например, отправку уведомления или запись в базу данных
                    }
                }
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Исключение: ${e.message}")
            }
        }
    }
}