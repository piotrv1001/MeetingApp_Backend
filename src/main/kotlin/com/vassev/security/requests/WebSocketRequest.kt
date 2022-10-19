package com.vassev.security.requests

import kotlinx.serialization.Serializable

@Serializable
data class WebSocketRequest(
    val userId: String,
    val meetingId: String,
    val username: String
)
