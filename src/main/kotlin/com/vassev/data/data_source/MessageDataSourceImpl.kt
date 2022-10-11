package com.vassev.data.data_source

import com.vassev.domain.data_source.MessageDataSource
import com.vassev.domain.model.Message
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MessageDataSourceImpl(
    db: CoroutineDatabase
): MessageDataSource {

    private val messages = db.getCollection<Message>()

    override suspend fun insertMessage(message: Message): Boolean {
        return messages.insertOne(message).wasAcknowledged()
    }

    override suspend fun getAllMessagesForMeeting(meetingId: String): List<Message> {
        return messages.find(Message::meetingId eq meetingId).toList()
    }
}