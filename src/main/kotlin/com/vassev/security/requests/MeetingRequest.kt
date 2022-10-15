package com.vassev.security.requests

import kotlinx.serialization.Serializable

@Serializable
data class MeetingRequest(
    val name: String,
    val duration: Int,
    val date: Long,
    val location: String,
    val users: List<String> = emptyList()
)
