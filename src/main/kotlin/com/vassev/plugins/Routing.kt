package com.vassev.plugins

import com.vassev.domain.data_source.MeetingDataSource
import com.vassev.domain.data_source.MessageDataSource
import com.vassev.domain.data_source.PlanDataSource
import com.vassev.domain.data_source.UserDataSource
import com.vassev.routes.meeting
import com.vassev.routes.message
import com.vassev.routes.plan
import com.vassev.routes.user
import com.vassev.security.token.TokenConfig
import com.vassev.security.token.TokenService
import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(tokenConfig: TokenConfig) {
    val userDataSource by inject<UserDataSource>()
    val meetingDataSource by inject<MeetingDataSource>()
    val planDataSource by inject<PlanDataSource>()
    val messageDataSource by inject<MessageDataSource>()
    val tokenService by inject<TokenService>()
    install(Routing) {
        user(userDataSource, tokenService, tokenConfig)
        meeting(meetingDataSource)
        plan(planDataSource)
        message(messageDataSource)
    }
}
