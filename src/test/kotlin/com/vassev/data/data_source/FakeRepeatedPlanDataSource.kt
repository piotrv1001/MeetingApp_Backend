package com.vassev.data.data_source

import com.vassev.domain.data_source.RepeatedPlanDataSource
import com.vassev.domain.model.Plan
import com.vassev.domain.model.RepeatedPlan
import com.vassev.domain.model.SpecificDay

class FakeRepeatedPlanDataSource: RepeatedPlanDataSource {

    private val repeatedPlans = mutableListOf<RepeatedPlan>()

    override suspend fun insertRepeatedPlan(repeatedPlan: RepeatedPlan): Boolean {
        repeatedPlans.add(repeatedPlan)
        return true
    }

    override suspend fun getRepeatedPlanForUserOnDay(
        userId: String,
        dayOfWeek: Int,
        specificDay: SpecificDay
    ): RepeatedPlan? {
        return repeatedPlans.find { it.userId == userId && it.dayOfWeek == dayOfWeek && !it.except.contains(specificDay) }
    }

    override suspend fun getRepeatedPlanForUser(userId: String, dayOfWeek: Int): RepeatedPlan? {
        return repeatedPlans.find { it.userId == userId && it.dayOfWeek == dayOfWeek }
    }

    override suspend fun updateRepeatedPlan(repeatedPlan: RepeatedPlan): Boolean {
        return true
    }

    override suspend fun deleteRepeatedPlan(userId: String, dayOfWeek: Int): Boolean {
        return true
    }

    override suspend fun addPlanToRepeatedPlan(userId: String, dayOfWeek: Int, plan: Plan): Boolean {
        return true
    }

    override suspend fun deletePlanFromRepeatedPlan(userId: String, dayOfWeek: Int, plan: Plan): Boolean {
        return true
    }

    override suspend fun addExceptionOnSpecificDay(userId: String, dayOfWeek: Int, specificDay: SpecificDay): Boolean {
        return true
    }
}