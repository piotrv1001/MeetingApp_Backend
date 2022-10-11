package com.vassev.domain.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Plan(
    @BsonId
    val planId: String = ObjectId().toString(),
    val fromDate: Long,
    val toDate: Long,
    val userId: String
)
