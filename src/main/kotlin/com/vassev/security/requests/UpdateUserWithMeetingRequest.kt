package com.vassev.security.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserWithMeetingRequest(
    val userIds: List<String>,
    val meetingId: String
)
