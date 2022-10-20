package com.vassev.data.requests

import com.vassev.domain.model.SpecificDay
import kotlinx.serialization.Serializable

@Serializable
data class AddExceptionToRepeatedPlanRequest(
    val specificDay: SpecificDay,
    val repeatedPlanId: String
)
