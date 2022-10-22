package com.vassev.domain.service

import com.vassev.data.responses.GenerateMeetingTimeResponse
import com.vassev.domain.model.SpecificDay

interface GenerateMeetingTimeService {

    fun generateMeetingTime(today: SpecificDay, userIds: List<String>): List<GenerateMeetingTimeResponse>
}