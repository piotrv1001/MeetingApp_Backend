package com.vassev.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class WebSocketRequest(
    val userId: String,
    val meetingId: String,
    val username: String
)
