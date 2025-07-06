package com.kurtnettle.simsmsmanager.dummy

import android.app.Service
import android.content.Intent
import android.os.IBinder

class DummyHeadlessSmsSendService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopSelf(startId)
        return START_NOT_STICKY
    }
}