package com.vassev.routes

import com.vassev.domain.data_source.MessageDataSource
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.message(
    messageDataSource: MessageDataSource
) {
    route("/message") {
        get("/{meetingId}") {
            val meetingId = call.parameters["meetingId"] ?: ""
            if(call.request.queryParameters["lastMessage"] == "true") {
                val lastMessage = messageDataSource.getLatestMessageForMeeting(meetingId)
                if(lastMessage != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        lastMessage
                    )
                    return@get
                }
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            val messages = messageDataSource.getAllMessagesForMeeting(meetingId)
            call.respond(
                HttpStatusCode.OK,
                messages
            )
        }
    }
}