package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var action: NotificationCompat.Action
    private lateinit var fileName : String
    private val urls = HashMap<String, String>().also {
        it[GLIDE] = URL_GLIDE
        it[RETROFIT] = URL_RETROFIT
        it[LOAD_APP] = URL_LOADAPP
    }

    private lateinit var radioGroup: RadioGroup
    private lateinit var glideRadioButton : RadioButton
    private lateinit var loadAppRadioButton : RadioButton
    private lateinit var retrofitRadioButton : RadioButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        // radio group and button to send notification
        radioGroup = findViewById(R.id.radiogroup)
        glideRadioButton = findViewById(R.id.radio_glide)
        loadAppRadioButton = findViewById(R.id.radio_loadapp)
        retrofitRadioButton = findViewById(R.id.radio_retrofit)




        createChannel()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            when {
                glideRadioButton.isChecked -> {
                    fileName = GLIDE
                    download(urls[fileName]!!)
                }
                retrofitRadioButton.isChecked -> {
                    fileName = RETROFIT
                    download(urls[fileName]!!)
                }
                loadAppRadioButton.isChecked -> {
                    fileName = LOAD_APP
                    download(urls[fileName]!!)

                }
                else -> {
                    Toast.makeText(this, "Please select a repository", Toast.LENGTH_SHORT).show()
                    fileName = ""
                }
            }
            custom_button.buttonState = ButtonState.Loading
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            Toast.makeText(this@MainActivity, "Download Completed", Toast.LENGTH_SHORT).show()
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                var status = 0
                val filename: String
                val subtitle: String

                val q = DownloadManager.Query()
                q.setFilterById(id)
                val c: Cursor = getSystemService(DownloadManager::class.java).query(q)

                if (c.moveToFirst()) {
                    status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE))
                } else {
                    filename = this@MainActivity.fileName
                }
                c.close()
                subtitle = if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    "Success"
                } else {
                    "Failed"
                }

                createNotification(filename, subtitle)
                custom_button.buttonState = ButtonState.Completed

            }
        }
    }


    private fun download(s: String) {
        val request =
            DownloadManager.Request(Uri.parse(s))
                .setTitle(fileName)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val NOTIFICATION_ID = 0
        private const val REQUEST_CODE = 0

        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_STATUS = "EXTRA_STATUS"

        const val URL_GLIDE = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        const val URL_LOADAPP = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        const val URL_RETROFIT =  "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        const val GLIDE = "Glide - Image Loading Library by BumpTech<"
        const val RETROFIT = "Retrofit -Type-safe HTTP client for Android and Java by Square,Inc"
        const val LOAD_APP= "LoadApp - current repository by Udacity"

    }





    fun createNotification(filename: String, status: String) {

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager


        val intent = Intent(applicationContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val detailIntent = Intent(applicationContext, DetailActivity::class.java)
        detailIntent.putExtra(EXTRA_NAME, filename)
        detailIntent.putExtra(EXTRA_STATUS, status)


        val detailPendingIntent =
            PendingIntent.getActivity(
                applicationContext,
                REQUEST_CODE,
                detailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        action = NotificationCompat.Action(
            null,
            getString(R.string.notification_button),
            detailPendingIntent
        )
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(filename)
            .setSubText(status)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(action)


        notificationManager.notify(NOTIFICATION_ID, builder.build())


    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel =
                NotificationChannel(
                    CHANNEL_ID,
                    "LoadAppChannel",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { setShowBadge(false) }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Complete"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }


}
