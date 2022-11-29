package com.vassev.data.data_source

import com.vassev.domain.data_source.OneTimePlanDataSource
import com.vassev.domain.model.OneTimePlan
import com.vassev.domain.model.Plan
import com.vassev.domain.model.SpecificDay

class FakeOneTimePlanDataSource: OneTimePlanDataSource {

    private val oneTimePlans = mutableListOf<OneTimePlan>()

    override suspend fun insertOneTimePlan(oneTimePlan: OneTimePlan): Boolean {
        oneTimePlans.add(oneTimePlan)
        return true
    }

    override suspend fun getAllOneTimePlansForUser(userId: String): List<OneTimePlan> {
        return oneTimePlans
    }

    override suspend fun getOneTimePlanForUserOnDay(userId: String, specificDay: SpecificDay): OneTimePlan? {
        return oneTimePlans.find { it.specificDay == specificDay && it.userId == userId }
    }

    override suspend fun updateOneTimePlan(oneTimePlan: OneTimePlan): Boolean {
        return true
    }

    override suspend fun addPlanToOneTimePlan(userId: String, specificDay: SpecificDay, plan: Plan): Boolean {
        return true
    }

    override suspend fun deletePlanFromOneTimePlan(userId: String, specificDay: SpecificDay, plan: Plan): Boolean {
        return true
    }

    override suspend fun deleteOneTimePlan(userId: String, specificDay: SpecificDay): Boolean {
        return true
    }
}