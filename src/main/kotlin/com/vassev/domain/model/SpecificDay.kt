package com.vassev.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpecificDay(
    var day: Int,
    var month: Int,
    var year: Int
)
