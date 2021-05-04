package ie.cm.citynavigation

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class NotificationHelper(base: Context?) : ContextWrapper(base) {
  private val CHANNEL_NAME = "High priority channel"
  private val CHANNEL_ID = "com.example.notifications$CHANNEL_NAME"

  @RequiresApi(api = Build.VERSION_CODES.O)
  private fun createChannels() {
    val notificationChannel =
      NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

    notificationChannel.enableLights(true)
    notificationChannel.enableVibration(true)
    notificationChannel.description = "this is the description of the channel."
    notificationChannel.lightColor = Color.RED
    notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(notificationChannel)
  }

  fun sendHighPriorityNotification(title: String?, body: String?, activityName: Class<*>?) {
    val intent = Intent(this, activityName)
    val pendingIntent =
      PendingIntent.getActivity(this, 267, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val notification =
      NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_dialog_alert)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setStyle(
          NotificationCompat.BigTextStyle()
            .setSummaryText(getString(ie.cm.citynavigation.R.string.geofence)).setBigContentTitle(title)
            .bigText(body)
        )
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()
    NotificationManagerCompat.from(this).notify(Random().nextInt(), notification)
  }

  init {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createChannels()
    }
  }
}