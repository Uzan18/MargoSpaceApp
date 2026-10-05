package com.pemmob.margocoffe.viewmodel

sealed interface NotificationEvent {

    data class MarkAsRead(
        val notificationId: String
    ) : NotificationEvent

    data object MarkAllAsRead : NotificationEvent

    data object ClearAll : NotificationEvent
}
