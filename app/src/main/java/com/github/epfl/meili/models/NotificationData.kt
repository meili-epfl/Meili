package com.github.epfl.meili.models

/**
 * The data stored inside the notification
 */
data class NotificationData(
    var title: String = "",
    var message: String = "",
    var otheruid: String = ""
) {

    companion object {
        //url to send notification json to
        const val BASE_URL = "https://fcm.googleapis.com"

        //Meili FCM server key
        const val SERVER_KEY = "AAAAQMGOSZE:APA91bGNQOI6HlXae8xBeMAB30q2vsNZoNzsPpcxfO2ytTiJl4HLdiw6oQDtB6KlIdu06Qu5QSLsg3tAIgHOT4tTfErPG_cyrF1d9ULGwrOmY7swzgJ--bzjsJi1Iq9fUVY9M9zilggV"
        //used json conversion type
        const val CONTENT_TYPE = "application/json"
    }

}