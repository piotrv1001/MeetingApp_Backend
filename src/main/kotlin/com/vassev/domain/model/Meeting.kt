package com.vassev.domain.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Meeting(
    @BsonId
    val meetingId: String = ObjectId().toString(),
    val name: String,
    val duration: Int,
    val date: Long,
    val users: List<String> = emptyList()
)
