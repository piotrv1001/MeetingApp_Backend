package com.vassev.data.service

import com.vassev.data.responses.GenerateMeetingTimeResponse
import com.vassev.data.responses.PlanResponse
import com.vassev.domain.data_source.OneTimePlanDataSource
import com.vassev.domain.data_source.RepeatedPlanDataSource
import com.vassev.domain.model.Plan
import com.vassev.domain.model.SpecificDay
import com.vassev.domain.service.GenerateMeetingTimeService
import java.time.LocalDate

class GenerateMeetingTimeServiceImpl(
    repeatedPlanDataSource: RepeatedPlanDataSource,
    oneTimePlanDataSource: OneTimePlanDataSource
): GenerateMeetingTimeService {

    override fun generateMeetingTime(
        today: SpecificDay,
        userIds: List<String>
    ): List<GenerateMeetingTimeResponse> {

        val resultList = listOf<GenerateMeetingTimeResponse>()
        var date = LocalDate.of(today.year, today.month, today.day)

        for (i in 0..6) {
            date = date.plusDays(1)
            for (userId in userIds) {
                TODO("FANCY ALGO")
            }
        }

        return resultList
    }

    private fun getUsersPlans(planResponse: PlanResponse): List<Plan> {
        val oneTimePlanList = planResponse.oneTimePlan?.plans
        val repeatedPlanList = planResponse.repeatedPlan?.plans
        val resultList: MutableList<Plan> = ArrayList()
        if(oneTimePlanList != null) {
            resultList.addAll(oneTimePlanList)
        }
        if(repeatedPlanList != null) {
            resultList.addAll(repeatedPlanList)
        }
        return resultList
    }
}