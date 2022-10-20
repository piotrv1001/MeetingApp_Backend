package com.vassev.domain.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class OneTimePlan(
    @BsonId
    val oneTimePlanId: String = ObjectId().toString(),
    val specificDay: SpecificDay,
    val userId: String,
    val plans: List<Plan> = emptyList()
)
