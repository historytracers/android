// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Study Reminder"
        val message = intent.getStringExtra("message") ?: "Time to study!"
        NotificationHelper.showReminder(context, title, message)
    }
}
