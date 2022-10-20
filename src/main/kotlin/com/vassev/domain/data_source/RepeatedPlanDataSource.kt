package com.vassev.domain.data_source

import com.vassev.domain.model.RepeatedPlan
import com.vassev.domain.model.SpecificDay

interface RepeatedPlanDataSource {

    suspend fun  insertRepeatedPlan(repeatedPlan: RepeatedPlan): Boolean

    suspend fun getRepeatedPlanForUserOnDay(userId: String, dayOfWeek: Int, specificDay: SpecificDay): RepeatedPlan?

    suspend fun updateRepeatedPlan(repeatedPlan: RepeatedPlan): Boolean

    suspend fun deleteRepeatedPlan(repeatedPlan: RepeatedPlan): Boolean

    suspend fun addExceptionOnSpecificDay(repeatedPlanId: String, specificDay: SpecificDay): Boolean
}