package com.vassev.domain.data_source

import com.vassev.domain.model.Message

interface MessageDataSource {

    suspend fun insertMessage(message: Message): Boolean

    suspend fun getAllMessagesForMeeting(meetingId: String): List<Message>
}