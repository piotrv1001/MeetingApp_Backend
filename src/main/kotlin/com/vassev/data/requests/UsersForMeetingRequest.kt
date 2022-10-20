package com.vassev.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class UsersForMeetingRequest(
    val userIds: List<String>
)
