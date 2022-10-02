package com.mertyazi.newsapp.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mertyazi.newsapp.R
import com.mertyazi.newsapp.utils.Constants
import com.mertyazi.newsapp.view.activities.MainActivity

class NotifyWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification() {
        val notificationId = 0

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId)

        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationTitle = applicationContext.getString(R.string.notification_title)
        val notificationSubtitle = applicationContext.getString(R.string.notification_subtitle)

        val bitmap = applicationContext.vectorToBitmap(R.drawable.logo_news_app)
        val bigPic = NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)

        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val notification = NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL)
            .setContentTitle(notificationTitle)
            .setContentText(notificationSubtitle)
            .setSmallIcon(R.drawable.logo_news_app)
            .setLargeIcon(bitmap)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setStyle(bigPic)
            .setAutoCancel(true)

        notification.priority = NotificationCompat.PRIORITY_MAX

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(Constants.NOTIFICATION_CHANNEL)

            val ringtoneManager =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL,
                    Constants.NOTIFICATION_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(notificationId, notification.build())
    }

    private fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}