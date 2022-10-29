package com.vassev.domain.model

data class PlanWithType(
    val fromHour: Int,
    val toHour: Int,
    val fromMinute: Int,
    val toMinute: Int,
    val repeat: Boolean
): Comparable<PlanWithType> {

    override fun compareTo(other: PlanWithType): Int = when {
        this.toPlan().startTime() != other.toPlan().startTime() -> this.toPlan().startTime() compareTo other.toPlan().startTime()
        this.toPlan().endTime() != other.toPlan().endTime() -> this.toPlan().endTime() compareTo other.toPlan().endTime()
        else -> 0
    }

    fun toPlan(): Plan{
        return Plan(
            fromHour = fromHour,
            fromMinute = fromMinute,
            toMinute = toMinute,
            toHour = toHour
        )
    }
}
