package com.vassev.data.requests

import com.vassev.data.responses.GenerateMeetingTimeResponse
import kotlinx.serialization.Serializable

@Serializable
data class SaveMeetingTimeRequest(
    val meetingId: String,
    val generateTimeResponse: GenerateMeetingTimeResponse
)
