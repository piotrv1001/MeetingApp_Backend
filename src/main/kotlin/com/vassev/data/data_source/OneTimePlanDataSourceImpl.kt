package com.vassev.data.data_source

import com.vassev.domain.data_source.OneTimePlanDataSource
import com.vassev.domain.model.OneTimePlan
import com.vassev.domain.model.SpecificDay
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class OneTimePlanDataSourceImpl(
    db: CoroutineDatabase
): OneTimePlanDataSource {

    private val oneTimePlans = db.getCollection<OneTimePlan>()

    override suspend fun insertOneTimePlan(oneTimePlan: OneTimePlan): Boolean {
        return oneTimePlans.insertOne(oneTimePlan).wasAcknowledged()
    }

    override suspend fun getOneTimePlanForUserOnDay(userId: String, specificDay: SpecificDay): OneTimePlan? {
        return oneTimePlans.findOne(and(OneTimePlan::specificDay eq specificDay, OneTimePlan::userId eq userId))
    }

    override suspend fun updateOneTimePlan(oneTimePlan: OneTimePlan): Boolean {
        return oneTimePlans.updateOne(OneTimePlan::oneTimePlanId eq oneTimePlan.oneTimePlanId, oneTimePlan).wasAcknowledged()
    }

    override suspend fun deleteOneTimePlan(oneTimePlan: OneTimePlan): Boolean {
        return oneTimePlans.deleteOne(OneTimePlan::oneTimePlanId eq oneTimePlan.oneTimePlanId).wasAcknowledged()
    }
}