package com.vassev.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class MeetingsForUserRequest(
    val meetingIds: List<String>
)
