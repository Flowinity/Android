package com.troplo.privateuploader.api

import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.data.model.User

object TpuFunctions {
  fun image(link: String?, recipient: User?): String? {
      if(recipient?.avatar != null) {
        return "https://i.troplo.com/i/${recipient.avatar}"
      }
      if(link == null) {
        return null
      }
      return if(link.length >= 20) {
        "https://colubrina.troplo.com/usercontent/$link"
      } else {
        "https://i.troplo.com/i/$link"
      }
    }

  fun getChatName(chat: Chat?): String {
    if(chat == null) {
      return "Communications"
    }
    return if(chat.type == "direct") {
      chat.recipient?.username ?: "Deleted User"
    } else {
      chat.name
    }
  }
}