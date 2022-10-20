package com.vassev.routes

import com.vassev.chat_room.ChatSession
import com.vassev.chat_room.RoomController
import com.vassev.data.requests.WebSocketRequest
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.webSocketChat(
    roomController: RoomController
) {
    webSocket("/chat") {
        val session = call.sessions.get<ChatSession>()
        if(session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }
        val userId = session.userId
        val meetingId = session.meetingId
        val username = session.username
        try {
            roomController.onJoin(
                userId = userId,
                socket = this
            )
            incoming.consumeEach { frame ->
                if(frame is Frame.Text) {
                    roomController.sendMessage(
                        userId = userId,
                        meetingId = meetingId,
                        username = username,
                        text = frame.readText()
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            roomController.tryDisconnect(userId)
        }
    }
}