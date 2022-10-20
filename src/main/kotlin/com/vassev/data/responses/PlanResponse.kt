package com.vassev.data.responses

import com.vassev.domain.model.OneTimePlan
import com.vassev.domain.model.RepeatedPlan
import kotlinx.serialization.Serializable

@Serializable
data class PlanResponse(
    val oneTimePlan: OneTimePlan?,
    val repeatedPlan: RepeatedPlan?
)
