package com.vassev.security.requests

import kotlinx.serialization.Serializable

@Serializable
data class UsersForMeetingRequest(
    val userIds: List<String>
)
