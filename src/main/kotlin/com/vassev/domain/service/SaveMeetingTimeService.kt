package com.vassev.domain.service

import com.vassev.data.responses.GenerateMeetingTimeResponse

interface SaveMeetingTimeService {

    suspend fun saveMeetingTime(
        meetingId: String,
        generateMeetingTimeResponse: GenerateMeetingTimeResponse
    ): Boolean
}