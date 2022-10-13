package com.vassev.routes

import com.vassev.domain.data_source.MeetingDataSource
import com.vassev.domain.model.Meeting
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.meeting(
    meetingDataSource: MeetingDataSource
) {
    route("/meeting") {
        get("/{meetingId}") {
            val meetingId = call.parameters["meetingId"] ?: ""
            val meeting = meetingDataSource.getMeetingById(meetingId)
            if(meeting == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                meeting
            )
        }
        // get all meetings for 1 user
        get("/{userId}") {
            val userId = call.parameters["userId"] ?: ""
            val meetings = meetingDataSource.getAllMeetingsByUserId(userId)
            call.respond(
                HttpStatusCode.OK,
                meetings
            )
        }
        post("/{userId}") {
            val request = call.receiveOrNull<Meeting>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userId = call.parameters["userId"] ?: ""
            val userList = listOf(userId)
            val meeting = Meeting(
                name = request.name,
                duration = request.duration,
                date = request.date,
                users = userList
            )
            val wasAcknowledged = meetingDataSource.insertMeeting(meeting)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}