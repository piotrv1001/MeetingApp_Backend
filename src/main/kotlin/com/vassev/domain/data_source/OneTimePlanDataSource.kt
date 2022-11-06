package com.vassev.domain.data_source

import com.vassev.domain.model.OneTimePlan
import com.vassev.domain.model.Plan
import com.vassev.domain.model.SpecificDay

interface OneTimePlanDataSource {

    suspend fun insertOneTimePlan(oneTimePlan: OneTimePlan): Boolean

    suspend fun getAllOneTimePlansForUser(userId: String): List<OneTimePlan>

    suspend fun getOneTimePlanForUserOnDay(userId: String, specificDay: SpecificDay): OneTimePlan?

    suspend fun updateOneTimePlan(oneTimePlan: OneTimePlan): Boolean

    suspend fun addPlanToOneTimePlan(userId: String, specificDay: SpecificDay, plan: Plan): Boolean

    suspend fun deletePlanFromOneTimePlan(userId: String, specificDay: SpecificDay, plan: Plan): Boolean

    suspend fun deleteOneTimePlan(userId: String, specificDay: SpecificDay): Boolean
}