package com.vassev.routes

import com.vassev.chat_room.RoomController
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.webSocketChat(
    roomController: RoomController
) {
    webSocket("/chat") {
        val userId = call.request.queryParameters["userId"] ?: ""
        val meetingId = call.request.queryParameters["meetingId"] ?: ""
        if(userId == "" || meetingId == "") {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid userId or meetingId"))
            return@webSocket
        }
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