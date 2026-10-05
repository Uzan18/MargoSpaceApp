package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationUiState(
    val notifications: List<AppNotification> = emptyList()
) {
    val unreadCount: Int
        get() = notifications.count { !it.isRead }

    val hasUnread: Boolean
        get() = unreadCount > 0
}

class NotificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> =
        _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            NotificationStore.notifications.collect { notifications ->
                _uiState.update {
                    it.copy(notifications = notifications)
                }
            }
        }
    }

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.MarkAsRead ->
                NotificationStore.markAsRead(event.notificationId)

            NotificationEvent.MarkAllAsRead ->
                NotificationStore.markAllAsRead()

            NotificationEvent.ClearAll ->
                NotificationStore.clear()
        }
    }
}
