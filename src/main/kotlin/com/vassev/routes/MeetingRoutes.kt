package com.vassev.routes

import com.vassev.domain.data_source.MeetingDataSource
import com.vassev.domain.data_source.UserDataSource
import com.vassev.domain.model.Meeting
import com.vassev.data.requests.MeetingRequest
import com.vassev.data.requests.MeetingsForUserRequest
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.meeting(
    meetingDataSource: MeetingDataSource,
    userDataSource: UserDataSource
) {
    route("/meeting") {
        get {
            call.respond(
                HttpStatusCode.OK,
                meetingDataSource.getAllMeetings()
            )
        }
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
        get("/forUser") {
            val request = call.receiveOrNull<MeetingsForUserRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val meetings = meetingDataSource.getAllMeetingsForUser(request.meetingIds)
            call.respond(
                HttpStatusCode.OK,
                meetings
            )
        }
        post {
            val request = call.receiveOrNull<MeetingRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val meeting = Meeting(
                name = request.name,
                duration = request.duration,
                date = request.date,
                location = request.location,
                users = request.users
            )
            val newMeeting = meetingDataSource.insertMeeting(meeting)
            val meetingId = newMeeting.meetingId
            val wasAcknowledged = userDataSource.updateUsersWithMeeting(request.users, meetingId)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            call.respond(HttpStatusCode.OK)
        }
        put {
            val request = call.receiveOrNull<Meeting>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val wasAcknowledged = meetingDataSource.updateMeeting(request)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@put
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}