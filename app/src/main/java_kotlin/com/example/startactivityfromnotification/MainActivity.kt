package com.example.startactivityfromnotification

import android.Manifest
import android.annotation.SuppressLint
import android.app.ComponentCaller
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Action
import androidx.core.app.NotificationCompat.MessagingStyle
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.app.TaskStackBuilder
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    //private var notifications = mutableListOf<Message>()
    private var notificationId = 0
    private lateinit var btnComplete: Button
    private lateinit var etvInput: EditText
    private var counter = 0
    private lateinit var nm: NotificationManager

    @SuppressLint("NewApi")
    private var registerResultPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
//                if (Settings.canDrawOverlays(this)) {
//                } else {
//                    val intent = Intent(
//                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:$packageName")
//                    )
//                    startActivityForResult(intent, 1001)
//                }
                startNotification()
                //   startNotificationCustom()
            } else {
                Toast.makeText(this, "Permission deny", Toast.LENGTH_SHORT).show()
            }
        }


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnComplete = findViewById(R.id.btn_accept)
        btnComplete.setOnClickListener {
            checkPermissions()
        }
        etvInput = findViewById(R.id.etv_input)

        createChannelId0()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        if (requestCode == 1001) {
            if (Settings.canDrawOverlays(this)) {
                startNotification()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun startNotificationCustom() {
        val largeView = RemoteViews(this.packageName, R.layout.large_view)
        val smallView = RemoteViews(this.packageName, R.layout.small_view)

        smallView.setImageViewBitmap(
            R.id.img_of_small_view,
            BitmapFactory.decodeResource(resources, R.drawable.img_small)
        )

        val customNotification = NotificationCompat.Builder(this, "CHANNEL0")
            .setSmallIcon(R.drawable.ic_notification_32)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(smallView)
            .setCustomBigContentView(largeView)
            .setPriority(NotificationCompat.PRIORITY_MAX)
        Log.d("SVU", "22222")
        nm.notify(notificationId, customNotification.build())
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun startNotification() {
        val target = Intent(this, SpecialActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, notificationId, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        createShortcut(target)

        val person = Person.Builder().setName("VU").setImportant(true).build()
        val notification0 = NotificationCompat.Builder(this, "CHANNEL0")
            .setContentTitle("This is my notification")
            .setContentText(etvInput.text)
//            .setGroup("GROUP0")
            .setSmallIcon(R.drawable.ic_notification_32)
            .setLargeIcon(Utils.createBitmapFromResource(this, R.drawable.img_small))
            .setStyle(
                MessagingStyle(person)
                    .addMessage("Hi", System.currentTimeMillis(), person)
//                BigPictureStyle()
//                    .bigPicture(Utils.createBitmapFromResource(this, R.drawable.img_big))
//                    .bigLargeIcon(Utils.createBitmapFromResource(null, null))
            ).setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .addPerson(person)
            .setBubbleMetadata(createBubbleMetaData())
            .setShortcutId(SHORTCUT_ID + counter)
            .addAction(createActionReply())
            .setAutoCancel(true)
        nm.notify(notificationId++, notification0.build())
    }

    @SuppressLint("InlinedApi")
    private fun createActionReply(): Action {
        Log.d("SVU", "create action reply 0")
        val remoteInput = RemoteInput.Builder("KEY0")
            .run {
                setLabel("Please input your information!")
                build()
            }
        val pendingIntent = PendingIntent.getBroadcast(
            this, notificationId,
            Intent(
                this,
                ReplyMessage::class.java
            ).setAction("com.example.startactivityfromnotification"),
            PendingIntent.FLAG_MUTABLE
                    or PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT
                    or PendingIntent.FLAG_UPDATE_CURRENT
        )
        Log.d("SVU", "create action reply 1")
        val action = Action.Builder(R.drawable.ic_notification_32, "Reply", pendingIntent)
            .addRemoteInput(remoteInput)
            .build()
        Log.d("SVU", "create action reply 2")
        return action
    }

    private fun createChannelId0() {
        nm = getSystemService(NotificationManager::class.java)
        val channel0 = NotificationChannel(
            "CHANNEL0",
            "Channel 0",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for bubble notifications"
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
        }
        nm.createNotificationChannel(channel0)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkPermissions() {
        val postNotification =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.POST_NOTIFICATIONS
            else null
        if (postNotification?.let {
                ActivityCompat.checkSelfPermission(
                    this,
                    it
                )
            } == PackageManager.PERMISSION_GRANTED) {
            registerResultPermission.launch(postNotification)
        } else {
            if (postNotification != null) {
                registerResultPermission.launch(postNotification)
            }
        }
    }

    private fun generateNormalActivity(): PendingIntent? {
        val intent = Intent(this, NormalActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val bstack = TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(intent)
        val pendingIntent = bstack.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun generateSpecialActivity(): PendingIntent? {
        val intent = Intent(this, SpecialActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, notificationId, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return pendingIntent
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun createBubbleMetaData(): NotificationCompat.BubbleMetadata? {
        val pi = generateSpecialActivity()
        val icon = IconCompat.createWithBitmap(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.img_small
            )
        )
        Log.d("SVU", "" + icon)
        if (pi != null && icon != null) {
            Log.d("SVU", "DK")
            return NotificationCompat.BubbleMetadata.Builder(pi, icon)
                .setAutoExpandBubble(true)
                .setDesiredHeight(600)
                .setSuppressNotification(true)
                .build()
        }
        return null
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createShortcut(target: Intent): ShortcutInfo {
        val shortcut = ShortcutInfo.Builder(this, SHORTCUT_ID + ++counter)
            .setCategories(mutableSetOf("Mess"))
            .setShortLabel("CHUA BIET")
            .setLongLived(true)
            .setIcon(
                Icon.createWithBitmap(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.img_small
                    )
                )
            )
            .setLongLabel("HIHI")

            .setIntent(target.setAction(Intent.ACTION_DEFAULT))
            .build()
        val shortcutManager = getSystemService(SHORTCUT_SERVICE) as ShortcutManager
        shortcutManager.dynamicShortcuts = mutableListOf(shortcut)
        return shortcut
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.Q)
        val locusId = "HIHI"
        const val SHORTCUT_ID = "com.example.startactivityfromnotification.TEST"
    }
}