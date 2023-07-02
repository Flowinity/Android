package com.troplo.privateuploader.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.UserStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class NotificationTypes {
    FRIEND_REQUEST {
        override fun toString(): String {
            return "Friend Request"
        }
    },
    GENERIC {
        override fun toString(): String {
            return "Announcement"
        }
    }
}

@Composable
fun NotificationsScreen() {
    val user = UserStore.user.collectAsState()
    val viewModel = remember { NotificationViewModel() }

    LaunchedEffect(Unit) {
        viewModel.markAsRead()
    }

    LazyColumn {
        user.value?.notifications?.forEach { notification ->
            item(
                key = notification.id
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = if(notification.dismissed) {
                            MaterialTheme.colorScheme.background
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer
                        }
                    ),
                    headlineContent = {
                        Text(notification.message)
                    },
                    supportingContent = {
                        Text(getTypeByRoute(notification.route).toString())
                    },
                    trailingContent = {
                        Text(TpuFunctions.formatDate(notification.createdAt).toString())
                    }
                )
            }
        }
    }
}

fun getTypeByRoute(route: String?): NotificationTypes {
    return if(route?.startsWith("/u/") == true) {
        NotificationTypes.FRIEND_REQUEST
    } else {
        NotificationTypes.GENERIC
    }
}

class NotificationViewModel: ViewModel() {
    fun markAsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.markNotificationsAsRead().execute()
            if(response.isSuccessful) {
                // set all to dismissed
                UserStore.user.value?.notifications.let { notifications ->
                    notifications?.forEach { notification ->
                        notification.dismissed = true
                    }
                    UserStore.user.value?.notifications = notifications ?: listOf()
                }
            }
        }
    }
}