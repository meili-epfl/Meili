package com.github.epfl.meili.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.messages.ChatLogActivity
import com.github.epfl.meili.profile.friends.FriendsListActivity
import com.github.epfl.meili.profile.friends.FriendsListActivity.Companion.FRIEND_KEY
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

/**
 * Custom service to receive notifications
 */
class FirebaseNotificationService : FirebaseMessagingService() {
    companion object{
        private const val CHANNEL_ID = "my_channel"

        var sharedPref: SharedPreferences? = null

        var token:String?
        get(){
            return sharedPref?.getString("token", "")
        }
        set(value){
            sharedPref?.edit()?.putString("token", value)?.apply()
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d("notif","here")
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        //intent to be launched when we click on the notificaiton
/*        val intent = Intent(this, ChatLogActivity::class.java)
            .putExtra(FRIEND_KEY,Auth.getCurrentUser())*/
        val intent = Intent(this, MapActivity::class.java)
        //clearing all intents (start fresh)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //intent can only be used once
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        //The android given notificaiton manager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //notification id needed to initialize notification
        val notificationID = Random.nextInt()

        //since android oreo you can customize the notification channel so you have to define it.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }
        //build notification
        val notification =  NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentTitle(message.data["message"])
            .setSmallIcon(R.mipmap.meili_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "ChannelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply{
            description = "My Channel description"
            enableLights(true)
            lightColor = Color.CYAN
        }
        notificationManager.createNotificationChannel(channel)
    }
}