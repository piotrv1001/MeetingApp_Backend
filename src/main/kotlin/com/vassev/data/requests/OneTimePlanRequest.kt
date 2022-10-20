package com.vassev.data.requests

import com.vassev.domain.model.Plan
import com.vassev.domain.model.SpecificDay
import kotlinx.serialization.Serializable

@Serializable
data class OneTimePlanRequest(
    val specificDay: SpecificDay,
    val userId: String,
    val plans: List<Plan> = emptyList()
)
