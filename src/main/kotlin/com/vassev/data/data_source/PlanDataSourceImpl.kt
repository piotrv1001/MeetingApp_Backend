package com.vassev.data.data_source

import com.vassev.domain.data_source.PlanDataSource
import com.vassev.domain.model.Plan
import org.litote.kmongo.coroutine.CoroutineDatabase

class PlanDataSourceImpl(
    db: CoroutineDatabase
): PlanDataSource {

    private val plans = db.getCollection<Plan>()

    override suspend fun deletePlanById(planId: String): Boolean {
        return plans.deleteOneById(planId).wasAcknowledged()
    }

    override suspend fun insertPlan(plan: Plan): Boolean {
        return plans.insertOne(plan).wasAcknowledged()
    }
}