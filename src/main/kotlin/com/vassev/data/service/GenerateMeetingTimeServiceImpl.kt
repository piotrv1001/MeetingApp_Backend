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
    private val repeatedPlanDataSource: RepeatedPlanDataSource,
    private val oneTimePlanDataSource: OneTimePlanDataSource
): GenerateMeetingTimeService {

    override suspend fun generateMeetingTime(
        today: SpecificDay,
        userIds: List<String>,
        duration: Int
    ): List<GenerateMeetingTimeResponse> {

        val resultList = mutableListOf<GenerateMeetingTimeResponse>()
        var date = LocalDate.of(today.year, today.month, today.day)

        for (day in 0..6) {
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
                    merge(mergedList, userPlans)
                }
                val filteredList = filter(mergedList, duration)
                resultList.add(
                    GenerateMeetingTimeResponse(
                        specificDay = currentSpecificDay,
                        plans = filteredList
                    )
                )
            }
        }

        return resultList
    }

    private fun merge(currentList: List<Plan>, newList: List<Plan>): MutableList<Plan> {
        val mergedList = mutableListOf<Plan>()
        var i = 0
        var j = 0
        while(i < currentList.size && j < newList.size) {
            if(currentList[i].startTime() >= newList[j].startTime())
            {
                if(currentList[i].startTime() >= newList[j].endTime())
                {
                    j++
                } else {
                    if(currentList[i].endTime() >= newList[j].endTime())
                    {
                        mergedList.add(Plan(
                            fromHour = currentList[i].fromHour,
                            fromMinute = currentList[i].fromMinute,
                            toHour = newList[j].toHour,
                            toMinute = newList[j].toMinute
                        ))
                        j += 1
                    } else {
                        mergedList.add(Plan(
                            fromHour = currentList[i].fromHour,
                            fromMinute = currentList[i].fromMinute,
                            toHour = currentList[i].toHour,
                            toMinute = currentList[i].toMinute
                        ))
                        i += 1
                    }
                }
            } else {
                if(currentList[i].endTime() >= newList[j].startTime())
                {
                    if(currentList[i].endTime() >= newList[j].endTime())
                    {
                        mergedList.add(Plan(
                            fromHour = newList[j].fromHour,
                            fromMinute = newList[j].fromMinute,
                            toHour = newList[j].toHour,
                            toMinute = newList[j].toMinute
                        ))
                        j += 1
                    } else {
                        mergedList.add(Plan(
                            fromHour = newList[j].fromHour,
                            fromMinute = newList[j].fromMinute,
                            toHour = currentList[i].toHour,
                            toMinute = currentList[i].toMinute
                        ))
                        i += 1
                    }
                } else {
                    i += 1
                }
            }
        }
        return mergedList
    }

    private fun filter(mergedList: MutableList<Plan>, duration: Int): List<Plan> {
        return mergedList.filter { plan ->
            plan.isWithinRange(duration)
        }
    }

    private fun getUsersPlans(oneTimePlanList: List<Plan>?, repeatedPlanList: List<Plan>?): List<Plan> {
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