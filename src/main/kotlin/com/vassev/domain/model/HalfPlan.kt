package com.vassev.domain.model

data class HalfPlan(
    val hour: Int,
    val minute: Int
) {
    operator fun compareTo(halfPlan: HalfPlan): Int {
        return (this.hour * 60 + this.minute) - (halfPlan.hour * 60 + halfPlan.minute)
    }

    operator fun plus(halfPlan: HalfPlan): Int {
        return (this.hour * 60 + this.minute) + (halfPlan.hour * 60 + halfPlan.minute)
    }

    operator fun minus(halfPlan: HalfPlan): Int {
        return (this.hour * 60 + this.minute) - (halfPlan.hour * 60 + halfPlan.minute)
    }
}
