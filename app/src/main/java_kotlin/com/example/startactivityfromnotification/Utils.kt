package com.example.startactivityfromnotification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object Utils {
    val REPLY_MESSAGE = ReplyMessage()

    fun createBitmapFromResource(context: Context?, res: Int?): Bitmap? {
        if (context == null || res == null) return null
        return BitmapFactory.decodeResource(context.resources, res)
    }
}