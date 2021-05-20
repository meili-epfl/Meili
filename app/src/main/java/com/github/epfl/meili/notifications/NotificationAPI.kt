package com.github.epfl.meili.notifications

import com.github.epfl.meili.models.NotificationData.Companion.CONTENT_TYPE
import com.github.epfl.meili.models.NotificationData.Companion.SERVER_KEY
import com.github.epfl.meili.models.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * The interface to send to the web a notification given the notification data
 */
interface NotificationAPI {


    //annotations found on the FCM documentations
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    /**
     * @param notification the notification datat to be sent
     */
    suspend fun postNotification (
        @Body notification: PushNotification
    ):Response<ResponseBody>
}