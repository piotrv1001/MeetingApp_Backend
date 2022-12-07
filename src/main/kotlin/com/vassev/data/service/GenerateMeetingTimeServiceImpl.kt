package com.vassev.data.service

import com.vassev.data.requests.GenerateTimeRequest
import com.vassev.data.responses.GenerateMeetingTimeResponse
import com.vassev.data.responses.PlanResponse
import com.vassev.domain.data_source.OneTimePlanDataSource
import com.vassev.domain.data_source.RepeatedPlanDataSource
import com.vassev.domain.model.Plan
import com.vassev.domain.model.SpecificDay
import com.vassev.domain.service.GenerateMeetingTimeService
import java.time.LocalDate

class GenerateMeetingTimeServiceImpl(
    private val repeatedPlanDataSource: RepeatedPlanDataSource,
    private val oneTimePlanDataSource: OneTimePlanDataSource
): GenerateMeetingTimeService {

    override suspend fun generateMeetingTime(
        today: SpecificDay,
        userIds: List<String>,
        duration: Int,
        generateTimeRequest: GenerateTimeRequest
    ): List<GenerateMeetingTimeResponse> {

        val resultList = mutableListOf<GenerateMeetingTimeResponse>()
        var date = LocalDate.of(today.year, today.month, today.day)

        for (day in 0 until 7 * generateTimeRequest.numberOfWeeks) {
            date = date.plusDays(1)
            val currentSpecificDay = SpecificDay(
                day = date.dayOfMonth,
                month = date.monthValue,
                year = date.year
            )
            val currentDayOfWeek = date.dayOfWeek.value
            var mergedList = mutableListOf<Plan>()
            for (user in userIds.indices) {
                val userOneTimePlans = oneTimePlanDataSource.getOneTimePlanForUserOnDay(userIds[user], currentSpecificDay)
                val userRepeatedPlans = repeatedPlanDataSource.getRepeatedPlanForUserOnDay(userIds[user], currentDayOfWeek, currentSpecificDay)
                val userPlans = getUsersPlans(userOneTimePlans?.plans, userRepeatedPlans?.plans).toMutableList()
                mergedList = if(user == 0) {
                    userPlans
                } else {
                    mergeList(mergedList, userPlans, duration)
                }
            }
            if(mergedList.isNotEmpty()) {
                mergedList.forEach { plan ->
                    resultList.add(
                        GenerateMeetingTimeResponse(
                            specificDay = currentSpecificDay,
                            plan = plan
                        )
                    )
                }
                if(generateTimeRequest.numberOfResults != 0 && resultList.size == generateTimeRequest.numberOfResults) {
                    return resultList
                }
            }
        }
        return resultList
    }

    private fun mergeList(currentList: List<Plan>, newList: List<Plan>, duration: Int): MutableList<Plan> {
        val mergedList = mutableListOf<Plan>()
        var i = 0
        var j = 0
        while(i < currentList.size && j < newList.size) {
            if(currentList[i].startTime() >= newList[j].startTime())
            {
                if(currentList[i].startTime() >= newList[j].endTime())
                {
                    j += 1
                } else {
                    if(currentList[i].endTime() >= newList[j].endTime())
                    {
                        val newPlan = Plan(
                            fromHour = currentList[i].fromHour,
                            fromMinute = currentList[i].fromMinute,
                            toHour = newList[j].toHour,
                            toMinute = newList[j].toMinute
                        )
                        if(newPlan.isWithinRange(duration)) {
                            mergedList.add(newPlan)
                        }
                        j += 1
                    } else {
                        val newPlan = Plan(
                            fromHour = currentList[i].fromHour,
                            fromMinute = currentList[i].fromMinute,
                            toHour = currentList[i].toHour,
                            toMinute = currentList[i].toMinute
                        )
                        if(newPlan.isWithinRange(duration)) {
                            mergedList.add(newPlan)
                        }
                        i += 1
                    }
                }
            } else {
                if(currentList[i].endTime() >= newList[j].startTime())
                {
                    if(currentList[i].endTime() >= newList[j].endTime())
                    {
                        val newPlan = Plan(
                            fromHour = newList[j].fromHour,
                            fromMinute = newList[j].fromMinute,
                            toHour = newList[j].toHour,
                            toMinute = newList[j].toMinute
                        )
                        if(newPlan.isWithinRange(duration)) {
                            mergedList.add(newPlan)
                        }
                        j += 1
                    } else {
                        val newPlan = Plan(
                            fromHour = newList[j].fromHour,
                            fromMinute = newList[j].fromMinute,
                            toHour = currentList[i].toHour,
                            toMinute = currentList[i].toMinute
                        )
                        if(newPlan.isWithinRange(duration)) {
                            mergedList.add(newPlan)
                        }
                        i += 1
                    }
                } else {
                    i += 1
                }
            }
        }
        return mergedList
    }

    private fun getUsersPlans(oneTimePlanList: List<Plan>?, repeatedPlanList: List<Plan>?): List<Plan> {
        val resultList: MutableList<Plan> = ArrayList()
        if(oneTimePlanList != null) {
            for(oneTimePLan in oneTimePlanList) {
                if(oneTimePLan.name == "" && oneTimePLan.meetingId == "") {
                    resultList.add(oneTimePLan)
                }
            }
        }
        if(repeatedPlanList != null) {
            resultList.addAll(repeatedPlanList)
        }
        return resultList.sorted()
    }
}