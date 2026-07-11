// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app

import android.app.Application
import com.historytracers.app.notification.NotificationHelper

class HistoryTracersApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
