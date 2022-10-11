package com.vassev.routes

import com.vassev.domain.data_source.UserDataSource
import com.vassev.domain.model.User
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.user(
    userDataSource: UserDataSource
) {
    route("/user") {
        get {
            call.respond(
                HttpStatusCode.OK,
                userDataSource.getAllUsers()
            )
        }
        get("/{userId}") {
            val userId = call.parameters["userId"] ?: ""
            val user = userDataSource.getUserById(userId)
            if(user == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                user
            )
        }
        // get all users for 1 meeting
        get("/{meetingId}") {
            val meetingId = call.parameters["meetingId"] ?: ""
            val users = userDataSource.getAllUsersByMeetingId(meetingId)
            call.respond(
                HttpStatusCode.OK,
                users
            )
        }
        post("/register") {
            val request = call.receiveOrNull<User>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val user = User(
                email = request.email,
                password = request.password,
                name = request.name,
                location = request.location,
            )
            val wasAcknowledged = userDataSource.insertUser(user)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}