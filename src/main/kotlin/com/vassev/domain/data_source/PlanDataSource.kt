package com.vassev.domain.data_source

import com.vassev.domain.model.Plan

interface PlanDataSource {

    suspend fun insertPlan(plan: Plan): Boolean

    suspend fun deletePlanById(planId: String): Boolean

}