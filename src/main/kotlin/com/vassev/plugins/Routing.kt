package com.vassev.plugins

import com.vassev.domain.data_source.MeetingDataSource
import com.vassev.domain.data_source.MessageDataSource
import com.vassev.domain.data_source.PlanDataSource
import com.vassev.domain.data_source.UserDataSource
import com.vassev.routes.meeting
import com.vassev.routes.message
import com.vassev.routes.plan
import com.vassev.routes.user
import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userDataSource by inject<UserDataSource>()
    val meetingDataSource by inject<MeetingDataSource>()
    val planDataSource by inject<PlanDataSource>()
    val messageDataSource by inject<MessageDataSource>()
    install(Routing) {
        user(userDataSource)
        meeting(meetingDataSource)
        plan(planDataSource)
        message(messageDataSource)
    }
}
