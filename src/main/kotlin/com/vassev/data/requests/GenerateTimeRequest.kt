package com.vassev.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class GenerateTimeRequest(
    val numberOfWeeks: Int,
    val numberOfResults: Int
)
