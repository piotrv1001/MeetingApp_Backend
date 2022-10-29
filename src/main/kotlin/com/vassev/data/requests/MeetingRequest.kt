package com.vassev.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class MeetingRequest(
    val name: String,
    val duration: Int,
    val date: String,
    val location: String,
    val users: List<String> = emptyList()
)
