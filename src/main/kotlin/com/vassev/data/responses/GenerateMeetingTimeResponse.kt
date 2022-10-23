package com.vassev.data.responses

import com.vassev.domain.model.Plan
import com.vassev.domain.model.SpecificDay
import kotlinx.serialization.Serializable

@Serializable
data class GenerateMeetingTimeResponse(
    val specificDay: SpecificDay,
    val plans: List<Plan>
)
