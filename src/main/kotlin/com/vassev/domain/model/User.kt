package com.vassev.domain.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    @BsonId
    val userId: String = ObjectId().toString(),
    val email: String,
    val password: String,
    val name: String,
    val location: String,
    val meetings: List<String> = emptyList()
)
