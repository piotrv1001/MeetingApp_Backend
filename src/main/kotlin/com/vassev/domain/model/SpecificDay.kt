package com.vassev.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpecificDay(
    val day: Int,
    val month: Int,
    val year: Int
)
