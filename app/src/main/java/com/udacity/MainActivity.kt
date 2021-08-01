package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var fileName: String = ""
    private var status: String = ""
    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)




        createChannel(CHANNEL_ID, "Downloads")

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


        custom_button.setOnClickListener {

            when (downloading_radio_group.checkedRadioButtonId) {
                -1 -> Toast.makeText(applicationContext, "Please select the file to download", Toast.LENGTH_SHORT).show();
                R.id.glide -> {
                    download("https://github.com/bumptech/glide")
                    fileName = getString(R.string.glide)

                }
                R.id.loadApp -> {
                    download("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip")
                    fileName = getString(R.string.load_App)

                }
                else -> {
                    download("https://github.com/square/retrofit")
                    fileName = getString(R.string.Retrofit)
                }

            }


        }
    }


    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NotificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                    .apply {
                        setShowBadge(false)
                    }
            NotificationChannel.enableVibration(true)
            NotificationChannel.enableLights(true)
            NotificationChannel.lightColor = Color.RED
            NotificationChannel.description = "Time for breakfast"
            notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(NotificationChannel)
        }
    }


    private fun NotificationManager.sendNotification(applicationContext: Context, fileName: String, status: String) {
        var resultIntent = Intent(applicationContext, DetailActivity::class.java)
        resultIntent.putExtra("fileName", fileName)
        resultIntent.putExtra("status", status)

        pendingIntent = PendingIntent.getActivity(
                applicationContext,
                downloadID.toInt(),
                resultIntent,
                PendingIntent.FLAG_ONE_SHOT)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                .setContentText(applicationContext.getString(R.string.notification_description))
                .setContentTitle(applicationContext.getString(R.string.notification_title))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_assistant_black_24dp, getString(R.string.check_button), pendingIntent)

        notify(downloadID.toInt(), builder.build())
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()
            notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            id?.let {
                query.setFilterById(it)
            }

            val cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                when (cursor.getInt(columnIndex)) {
                    DownloadManager.STATUS_SUCCESSFUL -> status = "Success"
                    else -> status = "Fail"
                }

            }
            cursor.close()
            if (context != null) {
                custom_button.setState(ButtonState.Completed)

                notificationManager.sendNotification(context, fileName, status)
            }

        }

    }


    private fun download(URL: String) {
        custom_button.setState(ButtonState.Loading)

        val request =
                DownloadManager.Request(Uri.parse(URL))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }


    companion object {


        //  private const val URL ="https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    fun NotificationManager.cancelNotifications() {
        cancelAll()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)

    }
}
