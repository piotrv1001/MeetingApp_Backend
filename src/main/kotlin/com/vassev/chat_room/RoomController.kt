package com.vassev.chat_room

import com.vassev.domain.data_source.MessageDataSource
import com.vassev.domain.model.Message
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        userId: String,
        socket: WebSocketSession
    ) {
        members[userId] = Member(
            userId = userId,
            socket = socket
        )
    }

    suspend fun sendMessage(userId: String, meetingId: String ,text: String, username: String) {
        val message = Message(
            text = text,
            timestamp = System.currentTimeMillis(),
            userId = userId,
            meetingId = meetingId,
            username = username
        )
        messageDataSource.insertMessage(message)
        members.values.forEach{ member ->
            val parsedMessage = Json.encodeToString(message)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun tryDisconnect(userId: String) {
        members[userId]?.socket?.close()
        if(members.containsKey(userId)) {
            members.remove(userId)
        }
    }
}