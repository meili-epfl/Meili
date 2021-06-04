package com.github.epfl.meili.models

/**
 * Targeted Notification data
 */
data class PushNotification (
    //encapsulation was a better solution than inheritance in this case
    val data: NotificationData,
    //added the to field so we have a target to notify
    val to: String
)