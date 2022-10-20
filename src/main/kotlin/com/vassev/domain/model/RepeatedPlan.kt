package com.vassev.domain.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class RepeatedPlan(
    @BsonId
    val repeatedPlanId: String = ObjectId().toString(),
    val dayOfWeek: Int,
    val userId: String,
    val plans: List<Plan> = emptyList(),
    val except: List<SpecificDay> = emptyList()
)
