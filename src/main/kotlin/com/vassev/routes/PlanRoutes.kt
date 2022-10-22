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
            val existingPlan = oneTimePlanDataSource.getOneTimePlanForUserOnDay(request.userId, request.specificDay)
            if(existingPlan == null) {
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
            } else {
                val plan = request.plans[0]
                val userId = request.userId
                val specificDay = request.specificDay
                val wasAcknowledged = oneTimePlanDataSource.addPlanToOneTimePlan(userId, specificDay, plan)
                if(!wasAcknowledged) {
                    call.respond(HttpStatusCode.Conflict)
                    return@post
                }
                call.respond(HttpStatusCode.OK)
            }

        }
        delete("/oneTimePlan") {
            val request = call.receiveOrNull<OneTimePlanRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val wasAcknowledged = oneTimePlanDataSource.deleteOneTimePlan(
                userId = request.userId,
                specificDay = request.specificDay
            )
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@delete
            }
            call.respond(HttpStatusCode.OK)
        }
        put("/oneTimePlan/deletePlan") {
            val request = call.receiveOrNull<OneTimePlanRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val plan = request.plans[0]
            val userId = request.userId
            val specificDay = request.specificDay
            val wasAcknowledged = oneTimePlanDataSource.deletePlanFromOneTimePlan(userId, specificDay, plan)
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
            val existingPLan = repeatedPlanDataSource.getRepeatedPlanForUser(request.userId, request.dayOfWeek)
            if(existingPLan == null) {
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
            } else {
                val plan = request.plans[0]
                val userId = request.userId
                val dayOfWeek = request.dayOfWeek
                val wasAcknowledged = repeatedPlanDataSource.addPlanToRepeatedPlan(userId, dayOfWeek, plan)
                if(!wasAcknowledged) {
                    call.respond(HttpStatusCode.Conflict)
                    return@post
                }
                call.respond(HttpStatusCode.OK)
            }
        }
        delete ("/repeatedPlan"){
            val request = call.receiveOrNull<RepeatedPlanRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val wasAcknowledged = repeatedPlanDataSource.deleteRepeatedPlan(
                userId = request.userId,
                dayOfWeek = request.dayOfWeek
            )
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@delete
            }
            call.respond(HttpStatusCode.OK)
        }
        put("/repeatedPlan/deletePlan") {
            val request = call.receiveOrNull<RepeatedPlanRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val plan = request.plans[0]
            val userId = request.userId
            val dayOfWeek = request.dayOfWeek
            val wasAcknowledged = repeatedPlanDataSource.deletePlanFromRepeatedPlan(userId, dayOfWeek, plan)
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
            val wasAcknowledged = repeatedPlanDataSource.addExceptionOnSpecificDay(
                dayOfWeek = request.dayOfWeek,
                userId = request.userId,
                specificDay = request.specificDay
            )
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@put
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}