package com.vassev.routes

import com.vassev.domain.data_source.PlanDataSource
import com.vassev.domain.model.Plan
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.plan(
    planDataSource: PlanDataSource
) {
    route("/plan") {
        post {
            val request = call.receiveOrNull<Plan>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val plan = Plan(
                fromDate = request.fromDate,
                toDate = request.toDate,
                userId = request.userId
            )
            val wasAcknowledged = planDataSource.insertPlan(plan)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            call.respond(HttpStatusCode.OK)
        }
        delete("/{planId}") {
            val planId = call.parameters["planId"] ?: ""
            val wasAcknowledged = planDataSource.deletePlanById(planId)
            if(!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@delete
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}