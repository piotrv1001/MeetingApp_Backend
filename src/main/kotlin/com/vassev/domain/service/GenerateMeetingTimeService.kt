package com.vassev.domain.service

import com.vassev.data.requests.GenerateTimeRequest
import com.vassev.data.responses.GenerateMeetingTimeResponse
import com.vassev.domain.model.SpecificDay

interface GenerateMeetingTimeService {

    suspend fun generateMeetingTime(
        today: SpecificDay,
        userIds: List<String>,
        duration: Int,
        generateTimeRequest: GenerateTimeRequest
    ): List<GenerateMeetingTimeResponse>
}