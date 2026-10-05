package com.pemmob.margocoffe.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false
)

enum class NotificationType {
    ORDER,
    REWARD,
    PROMO,
    POINTS,
    MEMBERSHIP
}

object NotificationStore {

    private val _notifications = MutableStateFlow(
        listOf(
            AppNotification(
                id = "1",
                title = "Selamat Datang di Margo Space!",
                message = "Nikmati berbagai pilihan kopi dan kumpulkan poin rewards kamu.",
                type = NotificationType.PROMO,
                timestamp = "Baru saja"
            ),
            AppNotification(
                id = "2",
                title = "Promo Baru",
                message = "Nikmati promo spesial dari Margo Space hari ini.",
                type = NotificationType.PROMO,
                timestamp = "Hari ini"
            ),
            AppNotification(
                id = "3",
                title = "Poin Rewards",
                message = "Setiap pembelian akan memberikan tambahan poin rewards.",
                type = NotificationType.POINTS,
                timestamp = "Hari ini"
            )
        )
    )

    val notifications: StateFlow<List<AppNotification>> =
        _notifications.asStateFlow()

    fun add(
        title: String,
        message: String,
        type: NotificationType
    ) {
        _notifications.update { current ->
            listOf(
                AppNotification(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    message = message,
                    type = type,
                    timestamp = "Baru saja"
                )
            ) + current
        }
    }

    fun markAsRead(id: String) {
        _notifications.update { current ->
            current.map {
                if (it.id == id) {
                    it.copy(isRead = true)
                } else {
                    it
                }
            }
        }
    }

    fun markAllAsRead() {
        _notifications.update { current ->
            current.map { it.copy(isRead = true) }
        }
    }

    fun clear() {
        _notifications.value = emptyList()
    }
}
