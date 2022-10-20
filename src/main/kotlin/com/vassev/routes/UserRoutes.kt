package com.vassev.routes

import com.vassev.domain.data_source.UserDataSource
import com.vassev.domain.model.User
import com.vassev.data.requests.LoginRequest
import com.vassev.data.requests.RegisterRequest
import com.vassev.data.requests.UsersForMeetingRequest
import com.vassev.security.token.TokenClaim
import com.vassev.security.token.TokenConfig
import com.vassev.security.token.TokenResponse
import com.vassev.security.token.TokenService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.user(
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
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
        get("/forMeeting") {
            val request = call.receiveOrNull<UsersForMeetingRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val users = userDataSource.getAllUsersForMeeting(request.userIds)
            call.respond(
                HttpStatusCode.OK,
                users
            )
        }
        post("/register") {
            val request = call.receiveOrNull<RegisterRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val potentialUserWithSameEmail = userDataSource.getUserByEmail(request.email)
            if(potentialUserWithSameEmail != null) {
                call.respond(HttpStatusCode.Conflict)
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
        post("/login") {
            val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val requestedUser = userDataSource.getUserByEmail(request.email)
            if(requestedUser == null || requestedUser.password != request.password) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            val token = tokenService.generate(
                config = tokenConfig,
                TokenClaim(
                    name = "userId",
                    value = requestedUser.userId
                )
            )
            call.respond(
                status = HttpStatusCode.OK,
                message = TokenResponse(
                    token = token,
                    userId = requestedUser.userId
                )
            )
        }
        authenticate {
            get("/authenticate") {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}