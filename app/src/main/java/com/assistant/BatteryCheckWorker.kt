package com.assistant

import android.content.Context
import android.os.BatteryManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class BatteryCheckWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    companion object {
        const val SHARED_PREFS_NAME = "BatteryCheckWorkerPrefs"
        const val KEY_BATTERY_ALERT_SHOWN_30 = "batteryAlertShown_30"
        const val KEY_BATTERY_ALERT_SHOWN_20 = "batteryAlertShown_20"
        const val KEY_BATTERY_ALERT_SHOWN_10 = "batteryAlertShown_10"
        const val KEY_BATTERY_ALERT_SHOWN_5 = "batteryAlertShown_5"
        const val KEY_BATTERY_ALERT_SHOWN_1 = "batteryAlertShown_1"
    }

    override fun doWork(): Result {
        Log.d("BatteryCheckWorker", "doWork")

        val batteryStatus = applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryStatus.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val isPluggedIn = batteryStatus.isCharging
        val sharedPreferences = applicationContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

        if (isPluggedIn) {
            sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT_SHOWN_30, false).apply()
            sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT_SHOWN_20, false).apply()
            sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT_SHOWN_10, false).apply()
            sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT_SHOWN_5, false).apply()
            sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT_SHOWN_1, false).apply()

            return Result.success()
        }

        val batteryAlertShown30 = sharedPreferences.getBoolean(KEY_BATTERY_ALERT_SHOWN_30, false)
        val batteryAlertShown20 = sharedPreferences.getBoolean(KEY_BATTERY_ALERT_SHOWN_20, false)
        val batteryAlertShown10 = sharedPreferences.getBoolean(KEY_BATTERY_ALERT_SHOWN_10, false)
        val batteryAlertShown5 = sharedPreferences.getBoolean(KEY_BATTERY_ALERT_SHOWN_5, false)
        val batteryAlertShown1 = sharedPreferences.getBoolean(KEY_BATTERY_ALERT_SHOWN_1, false)

        val messageText = "Заряд батареи: $batteryLevel%"

        val token = BuildConfig.TELEGRAM_BOT_TOKEN
        val chatId = BuildConfig.TELEGRAM_CHAT_ID
        val telegramAdapter = TelegramAdapter(token, chatId)

        if (batteryLevel in 21..29 && !batteryAlertShown30) {
            telegramAdapter.sendMessage(messageText)
            sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT_SHOWN_30, true).apply()
        }

        if (batteryLevel in 11..19 && !batteryAlertShown20) {
            telegramAdapter.sendMessage(messageText)
            sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT_SHOWN_20, true).apply()
        }

        if (batteryLevel in 6..9 && !batteryAlertShown10) {
            telegramAdapter.sendMessage(messageText)
            sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT_SHOWN_10, true).apply()
        }

        if (batteryLevel in 2..5 && !batteryAlertShown5) {
            telegramAdapter.sendMessage(messageText)
            sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT_SHOWN_5, true).apply()
        }

        if (batteryLevel == 1 && !batteryAlertShown1) {
            telegramAdapter.sendMessage(messageText)
            sharedPreferences.edit().putBoolean(KEY_BATTERY_ALERT_SHOWN_1, true).apply()
        }

        return Result.success()
    }
}