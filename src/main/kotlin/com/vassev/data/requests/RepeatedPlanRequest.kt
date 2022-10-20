package com.vassev.data.requests

import com.vassev.domain.model.Plan
import com.vassev.domain.model.SpecificDay
import kotlinx.serialization.Serializable

@Serializable
data class RepeatedPlanRequest(
    val dayOfWeek: Int,
    val userId: String,
    val plans: List<Plan> = emptyList(),
    val except: List<SpecificDay> = emptyList()
)
