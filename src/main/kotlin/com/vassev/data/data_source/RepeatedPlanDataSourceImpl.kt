package com.vassev.data.data_source

import com.vassev.domain.data_source.RepeatedPlanDataSource
import com.vassev.domain.model.Plan
import com.vassev.domain.model.RepeatedPlan
import com.vassev.domain.model.SpecificDay
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.updateOne

class RepeatedPlanDataSourceImpl(
    db: CoroutineDatabase
): RepeatedPlanDataSource {

    private val repeatedPlans = db.getCollection<RepeatedPlan>()

    override suspend fun insertRepeatedPlan(repeatedPlan: RepeatedPlan): Boolean {
        return repeatedPlans.insertOne(repeatedPlan).wasAcknowledged()
    }

    override suspend fun getRepeatedPlanForUser(userId: String, dayOfWeek: Int): RepeatedPlan? {
        return repeatedPlans.findOne(and(RepeatedPlan::userId eq userId, RepeatedPlan::dayOfWeek eq dayOfWeek))
    }

    override suspend fun getRepeatedPlanForUserOnDay(userId: String, dayOfWeek: Int, specificDay: SpecificDay): RepeatedPlan? {
        return repeatedPlans.findOne(and(RepeatedPlan::userId eq userId, RepeatedPlan::dayOfWeek eq dayOfWeek, not(RepeatedPlan::except contains specificDay)))
    }

    override suspend fun updateRepeatedPlan(repeatedPlan: RepeatedPlan): Boolean {
        return repeatedPlans.updateOne(RepeatedPlan::repeatedPlanId eq repeatedPlan.repeatedPlanId, repeatedPlan).wasAcknowledged()
    }

    override suspend fun deleteRepeatedPlan(repeatedPlan: RepeatedPlan): Boolean {
        return repeatedPlans.deleteOne(RepeatedPlan::repeatedPlanId eq repeatedPlan.repeatedPlanId).wasAcknowledged()
    }

    override suspend fun addExceptionOnSpecificDay(repeatedPlanId: String, specificDay: SpecificDay): Boolean {
        return repeatedPlans.updateOne(RepeatedPlan::repeatedPlanId eq repeatedPlanId, addToSet(RepeatedPlan::except, specificDay)).wasAcknowledged()
    }

    override suspend fun addPlanToRepeatedPlan(userId: String, dayOfWeek: Int, plan: Plan): Boolean {
        return repeatedPlans.updateOne(and(RepeatedPlan::dayOfWeek eq dayOfWeek, RepeatedPlan::userId eq userId), addToSet(RepeatedPlan::plans, plan)).wasAcknowledged()
    }

    override suspend fun deletePlanFromRepeatedPlan(userId: String, dayOfWeek: Int, plan: Plan): Boolean {
        return repeatedPlans.updateOne(and(RepeatedPlan::dayOfWeek eq dayOfWeek, RepeatedPlan::userId eq userId), pull(RepeatedPlan::plans, plan)).wasAcknowledged()
    }
}