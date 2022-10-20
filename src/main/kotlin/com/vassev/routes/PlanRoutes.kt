package com.vassev.routes

import com.vassev.data.requests.AddExceptionToRepeatedPlanRequest
import com.vassev.data.requests.OneTimePlanRequest
import com.vassev.data.requests.PlanRequest
import com.vassev.data.requests.RepeatedPlanRequest
import com.vassev.data.responses.PlanResponse
import com.vassev.domain.data_source.OneTimePlanDataSource
import com.vassev.domain.data_source.RepeatedPlanDataSource
import com.vassev.domain.model.OneTimePlan
import com.vassev.domain.model.RepeatedPlan
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.plan(
    oneTimePlanDataSource: OneTimePlanDataSource,
    repeatedPlanDataSource: RepeatedPlanDataSource
) {
    route("/plan") {
        get {
            val request = call.receiveOrNull<PlanRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val userId = request.userId
            val specificDay = request.specificDay
            val dayOfWeek = request.dayOfWeek
            val repeatedPlan = repeatedPlanDataSource.getRepeatedPlanForUserOnDay(userId, dayOfWeek, specificDay)
            val oneTimePlan = oneTimePlanDataSource.getOneTimePlanForUserOnDay(userId, specificDay)
            val response = PlanResponse(
                oneTimePlan = oneTimePlan,
                repeatedPlan = repeatedPlan
            )
            call.respond(
                HttpStatusCode.OK,
                response
            )
        }
        post("/oneTimePlan") {
            val request = call.receiveOrNull<OneTimePlanRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val oneTimePlan = OneTimePlan(
                specificDay = request.specificDay,
                userId = request.userId,
                plans = request.plans
            )
            val wasAcknowledged = oneTimePlanDataSource.insertOneTimePlan(oneTimePlan)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            call.respond(HttpStatusCode.OK)
        }
        put("/oneTimePlan") {
            val request = call.receiveOrNull<OneTimePlanRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val oneTimePlan = OneTimePlan(
                specificDay = request.specificDay,
                userId = request.userId,
                plans = request.plans
            )
            val wasAcknowledged = oneTimePlanDataSource.updateOneTimePlan(oneTimePlan)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@put
            }
            call.respond(HttpStatusCode.OK)
        }
        post("/repeatedPlan") {
            val request = call.receiveOrNull<RepeatedPlanRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val repeatedPlan = RepeatedPlan(
                dayOfWeek = request.dayOfWeek,
                userId = request.userId,
                plans = request.plans,
                except = request.except
            )
            val wasAcknowledged = repeatedPlanDataSource.insertRepeatedPlan(repeatedPlan)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            call.respond(HttpStatusCode.OK)
        }
        put("/repeatedPlan") {
            val request = call.receiveOrNull<RepeatedPlanRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val repeatedPlan = RepeatedPlan(
                dayOfWeek = request.dayOfWeek,
                userId = request.userId,
                plans = request.plans,
                except = request.except
            )
            val wasAcknowledged = repeatedPlanDataSource.updateRepeatedPlan(repeatedPlan)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@put
            }
            call.respond(HttpStatusCode.OK)
        }
        put("/repeatedPlan/except") {
            val request = call.receiveOrNull<AddExceptionToRepeatedPlanRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val specificDay = request.specificDay
            val repeatedPlanId = request.repeatedPlanId
            val wasAcknowledged = repeatedPlanDataSource.addExceptionOnSpecificDay(repeatedPlanId, specificDay)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@put
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}