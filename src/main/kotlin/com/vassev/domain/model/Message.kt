package com.vassev.domain.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Message(
    @BsonId
    val messageId: String = ObjectId().toString(),
    val text: String,
    val timestamp: Long,
    val userId: String,
    val meetingId: String,
    val username: String
)
