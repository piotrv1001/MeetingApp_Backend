package com.vassev.chat_room

import io.ktor.http.cio.websocket.*

data class Member(
    val userId: String,
    val socket: WebSocketSession
)