package com.example.startactivityfromnotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.RemoteInput

class ReplyMessage: BroadcastReceiver() {
     var msg: String = "NULL"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("SVU", "0")
        if (intent?.action.equals("com.example.startactivityfromnotification")) {
            Log.d("SVU", "1")
            if (intent?.let { RemoteInput.getResultsFromIntent(intent) } != null) {
                Log.d("SVU", "2")
                Utils.REPLY_MESSAGE.msg =
                    RemoteInput.getResultsFromIntent(intent)?.getCharSequence("KEY0").toString()
            }
        }
    }
}