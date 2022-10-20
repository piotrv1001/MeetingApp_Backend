package com.vassev.plugins

import com.vassev.chat_room.RoomController
import com.vassev.domain.data_source.*
import com.vassev.routes.*
import com.vassev.security.token.TokenConfig
import com.vassev.security.token.TokenService
import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(tokenConfig: TokenConfig) {
    val userDataSource by inject<UserDataSource>()
    val meetingDataSource by inject<MeetingDataSource>()
    val messageDataSource by inject<MessageDataSource>()
    val oneTimePlanDataSource by inject<OneTimePlanDataSource>()
    val repeatedPlanDataSource by inject<RepeatedPlanDataSource>()
    val roomController by inject<RoomController>()
    val tokenService by inject<TokenService>()
    install(Routing) {
        user(userDataSource, tokenService, tokenConfig)
        meeting(meetingDataSource, userDataSource)
        message(messageDataSource)
        plan(oneTimePlanDataSource, repeatedPlanDataSource)
        webSocketChat(roomController)
    }
}
