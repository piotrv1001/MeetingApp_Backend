package com.vassev.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Plan(
    val fromHour: Int,
    val toHour: Int,
    val fromMinute: Int,
    val toMinute: Int,
    val name: String = "",
    val meetingId: String = ""
): Comparable<Plan> {

    override fun compareTo(other: Plan): Int = when {
        this.startTime() != other.startTime() -> this.startTime() compareTo other.startTime()
        this.endTime() != other.endTime() -> this.endTime() compareTo other.endTime()
        else -> 0
    }

    fun startTime(): HalfPlan {
        return HalfPlan(
            hour = this.fromHour,
            minute = this.fromMinute
        )
    }

    fun endTime(): HalfPlan {
        return HalfPlan(
            hour = this.toHour,
            minute = this.toMinute
        )
    }

    fun isWithinRange(duration: Int): Boolean {
        return (this.toHour * 60 + this.toMinute) - (this.fromHour * 60 + this.fromMinute) >= duration
    }

    fun isWithinAnotherPlan(anotherPlan: Plan): Boolean {
        return this.startTime() >= anotherPlan.startTime() && this.endTime() <= anotherPlan.endTime()
    }

    fun isCloserToRange(anotherPlan: Plan, range: Plan): Boolean {
        val firstPlanDistance = if(this.startTime() > range.endTime()) {
            this.startTime() - range.endTime()
        } else {
            range.startTime() - this.endTime()
        }
        val anotherPlanDistance = if(anotherPlan.startTime() > range.endTime()) {
            anotherPlan.startTime() - range.endTime()
        } else {
            range.startTime() - anotherPlan.endTime()
        }
        return firstPlanDistance <= anotherPlanDistance
    }
}

