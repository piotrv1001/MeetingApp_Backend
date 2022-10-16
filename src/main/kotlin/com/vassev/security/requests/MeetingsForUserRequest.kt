package com.vassev.security.requests

import kotlinx.serialization.Serializable

@Serializable
data class MeetingsForUserRequest(
    val meetingIds: List<String>
)
