package com.vassev.routes

import com.vassev.data.requests.GenerateTimeRequest
import com.vassev.domain.data_source.MeetingDataSource
import com.vassev.domain.model.SpecificDay
import com.vassev.domain.service.GenerateMeetingTimeService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.LocalDate

fun Route.generateMeetingTime(
    meetingDataSource: MeetingDataSource,
    generateMeetingTimeService: GenerateMeetingTimeService
) {
    route("/generateTime") {
        get ("/{meetingId}"){
            val request = call.receiveOrNull<GenerateTimeRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val meetingId = call.parameters["meetingId"] ?: ""
            val meeting = meetingDataSource.getMeetingById(meetingId)
            if(meeting == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            val userIds = meeting.users
            val todayTime = LocalDate.now()
            val today = SpecificDay(
                day = todayTime.dayOfMonth,
                month = todayTime.monthValue,
                year = todayTime.year
            )
            val duration = meeting.duration
            val response = generateMeetingTimeService.generateMeetingTime(today, userIds, duration, request)
            call.respond(
                HttpStatusCode.OK,
                response
            )
        }
    }
}