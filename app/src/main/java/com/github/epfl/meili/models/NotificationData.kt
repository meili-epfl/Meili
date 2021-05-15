package com.github.epfl.meili.models

/**
 * The data stored inside the notification
 */
data class NotificationData(
    var title: String = "",
    var message: String = "",
) {

    companion object {
        //url to send notification json to
        const val BASE_URL = "https://fcm.googleapis.com"

        //Meili FCM server key
        const val SERVER_KEY = ""
        //used json conversion type
        const val CONTENT_TYPE = "application/json"
    }

}