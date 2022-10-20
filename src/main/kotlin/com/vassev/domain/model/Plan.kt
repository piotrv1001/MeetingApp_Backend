package com.vassev.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Plan(
    val fromHour: Int,
    val toHour: Int,
    val fromMinute: Int,
    val toMinute: Int,
)
