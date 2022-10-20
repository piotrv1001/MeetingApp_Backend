package com.vassev.data.requests

import com.vassev.domain.model.SpecificDay
import kotlinx.serialization.Serializable

@Serializable
data class PlanRequest(
    val specificDay: SpecificDay,
    val userId: String,
    val dayOfWeek: Int
)
