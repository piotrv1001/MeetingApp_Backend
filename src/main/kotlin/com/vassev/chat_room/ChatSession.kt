package com.vassev.chat_room

data class ChatSession(
    val sessionId: String,
    val userId: String,
    val meetingId: String
)
