package com.elinext.holidays.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.elinext.holidays.android.utils.Constants.MONTH
import com.elinext.holidays.android.utils.Constants.PUSH_RESTORED
import com.elinext.holidays.android.utils.Constants.YEAR
import com.elinext.holidays.utils.Constants
import java.time.YearMonth

class MyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val title: String? = intent.getStringExtra("Title")
        val description: String? = intent.getStringExtra("Description")
        val id: Int = intent.getIntExtra("id",1)
        val month = intent.getIntExtra("Month", YearMonth.now().monthValue)
        val notificationUtils = NotificationUtils(context)
        val notification = notificationUtils.getNotificationBuilder(title, description, month).build()
        notificationUtils.getManager().notify(id, notification)
        removeNotificationFromSP(context,month)
    }

    private fun removeNotificationFromSP(context: Context, month: Int) {
        val sf: SharedPreferences = context.getSharedPreferences(Constants.HOLIDAYS_APP, 0) ?: return
        val editor = sf.edit()
        editor.remove(Constants.NOTIFICATION_SP_KEY +month)
        editor.apply()
    }
}

class NotificationUtils(base: Context) : ContextWrapper(base) {
    val MYCHANNEL_ID = "HOLIDAYS Alert Notification ID"
    val MYCHANNEL_NAME = "HOLIDAYS Alert Notification"
    private var manager: NotificationManager? = null
    init {
        createChannels()
    }

    private fun createChannels() {
        val channel =
            NotificationChannel(MYCHANNEL_ID, MYCHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        channel.enableVibration(true)
        getManager().createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager {
        if (manager == null) manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager as NotificationManager
    }

    fun getNotificationBuilder(title: String?, description: String?, month: Int): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingFlags: Int =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingFlags)
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.bigText(description)
        return NotificationCompat.Builder(applicationContext, MYCHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setColor(Color.YELLOW)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setStyle(bigTextStyle)
            .setAutoCancel(true)
    }
}

data class Notification(
    val country: String,
    val description: String,
    val month: Int,
    val day: Int,
    val id: Int
)