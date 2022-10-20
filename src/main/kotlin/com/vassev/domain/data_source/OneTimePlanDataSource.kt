package com.vassev.domain.data_source

import com.vassev.domain.model.OneTimePlan
import com.vassev.domain.model.SpecificDay

interface OneTimePlanDataSource {

    suspend fun insertOneTimePlan(oneTimePlan: OneTimePlan): Boolean

    suspend fun getOneTimePlanForUserOnDay(userId: String, specificDay: SpecificDay): OneTimePlan?

    suspend fun updateOneTimePlan(oneTimePlan: OneTimePlan): Boolean

    suspend fun deleteOneTimePlan(oneTimePlan: OneTimePlan): Boolean
}