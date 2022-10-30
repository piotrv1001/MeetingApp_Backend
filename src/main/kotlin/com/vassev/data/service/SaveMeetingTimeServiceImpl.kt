package com.vassev.data.service

import com.vassev.data.responses.GenerateMeetingTimeResponse
import com.vassev.domain.data_source.MeetingDataSource
import com.vassev.domain.data_source.OneTimePlanDataSource
import com.vassev.domain.data_source.RepeatedPlanDataSource
import com.vassev.domain.model.*
import com.vassev.domain.service.SaveMeetingTimeService
import com.vassev.util.DateUtil
import java.time.LocalDate

class SaveMeetingTimeServiceImpl(
    private val repeatedPlanDataSource: RepeatedPlanDataSource,
    private val oneTimePlanDataSource: OneTimePlanDataSource,
    private val meetingDataSource: MeetingDataSource
): SaveMeetingTimeService {

    override suspend fun saveMeetingTime(meetingId: String, generateMeetingTimeResponse: GenerateMeetingTimeResponse): Boolean {
        val specificDay = generateMeetingTimeResponse.specificDay
        val date = LocalDate.of(specificDay.year, specificDay.month, specificDay.day)
        val currentDayOfWeek = date.dayOfWeek.value
        val day = specificDay.day
        val month = specificDay.month
        val year = specificDay.year
        val monthName = DateUtil.getMonthName(month)
        val fromHour = generateMeetingTimeResponse.plan.fromHour
        val fromMinute = generateMeetingTimeResponse.plan.fromMinute
        val toHour = generateMeetingTimeResponse.plan.toHour
        val toMinute = generateMeetingTimeResponse.plan.toMinute
        val formattedPlan = DateUtil.getFormattedPlan(
            fromHour = fromHour,
            fromMinute = fromMinute,
            toHour = toHour,
            toMinute = toMinute
        )
        val formattedDate = "$day $monthName $year, $formattedPlan"
        val meeting = meetingDataSource.updateMeetingDate(
            meetingId = meetingId,
            date = formattedDate
        )
        if(meeting != null) {
            for(user in meeting.users.indices) {
                val userOneTimePlans = oneTimePlanDataSource.getOneTimePlanForUserOnDay(meeting.users[user], specificDay)
                val userRepeatedPlans = repeatedPlanDataSource.getRepeatedPlanForUserOnDay(meeting.users[user], currentDayOfWeek, specificDay)
                val userPlans = getUsersPlans(userOneTimePlans?.plans, userRepeatedPlans?.plans)
                val newPlan = Plan(
                    fromHour = fromHour,
                    fromMinute = fromMinute,
                    toHour = toHour,
                    toMinute = toMinute
                )
                insertMeetingIntoCalendar(userPlans, newPlan, meeting.users[user], specificDay, currentDayOfWeek, meeting.name)
            }
            return true
        } else {
            return false
        }
    }

    private fun getUsersPlans(oneTimePlanList: List<Plan>?, repeatedPlanList: List<Plan>?): List<PlanWithType> {
        val resultList: MutableList<PlanWithType> = ArrayList()
        if(oneTimePlanList != null) {
            for(i in oneTimePlanList.indices) {
                resultList.add(
                    PlanWithType(
                        fromHour = oneTimePlanList[i].fromHour,
                        fromMinute = oneTimePlanList[i].fromMinute,
                        toHour = oneTimePlanList[i].toHour,
                        toMinute = oneTimePlanList[i].toMinute,
                        repeat = false
                    )
                )
            }
        }
        if(repeatedPlanList != null) {
            for(i in repeatedPlanList.indices) {
                resultList.add(
                    PlanWithType(
                        fromHour = repeatedPlanList[i].fromHour,
                        fromMinute = repeatedPlanList[i].fromMinute,
                        toHour = repeatedPlanList[i].toHour,
                        toMinute = repeatedPlanList[i].toMinute,
                        repeat = true
                    )
                )
            }
        }
        return resultList.sorted()
    }

    private suspend fun insertMeetingIntoCalendar(userPlans: List<PlanWithType>, newPlan: Plan, userId: String, specificDay: SpecificDay, dayOfWeek: Int, meetingName: String) {
        for(i in userPlans.indices) {
            if(newPlan.startTime() < userPlans[i].toPlan().endTime()) {
                if(userPlans[i].repeat) {
                    repeatedPlanDataSource.addExceptionOnSpecificDay(
                        userId = userId,
                        dayOfWeek = dayOfWeek,
                        specificDay = specificDay
                    )
                } else {
                    oneTimePlanDataSource.deletePlanFromOneTimePlan(
                        userId = userId,
                        specificDay = specificDay,
                        plan = userPlans[i].toPlan()
                    )
                }
                if(userPlans[i].toPlan().startTime() != newPlan.startTime()) {
                    val insertedPlan = Plan(
                        fromHour = userPlans[i].fromHour,
                        fromMinute = userPlans[i].fromMinute,
                        toHour = newPlan.fromHour,
                        toMinute = newPlan.fromMinute
                    )
                    val existingOneTimePlan = oneTimePlanDataSource.getOneTimePlanForUserOnDay(
                        userId = userId,
                        specificDay = specificDay
                    )
                    if(existingOneTimePlan != null) {
                        oneTimePlanDataSource.addPlanToOneTimePlan(
                            userId = userId,
                            specificDay = specificDay,
                            plan = insertedPlan
                        )
                    } else {
                        oneTimePlanDataSource.insertOneTimePlan(
                            OneTimePlan(
                                specificDay = specificDay,
                                userId = userId,
                                plans = listOf(insertedPlan)
                            )
                        )
                    }
                }
                val insertedPlanMiddle = Plan(
                    fromHour = newPlan.fromHour,
                    fromMinute = newPlan.fromMinute,
                    toHour = newPlan.toHour,
                    toMinute = newPlan.toMinute,
                    name = meetingName
                )
                val existingOneTimePlanMiddle = oneTimePlanDataSource.getOneTimePlanForUserOnDay(
                    userId = userId,
                    specificDay = specificDay
                )
                if(existingOneTimePlanMiddle != null) {
                    oneTimePlanDataSource.addPlanToOneTimePlan(
                        userId = userId,
                        specificDay = specificDay,
                        plan = insertedPlanMiddle
                    )
                } else {
                    oneTimePlanDataSource.insertOneTimePlan(
                        OneTimePlan(
                            specificDay = specificDay,
                            userId = userId,
                            plans = listOf(insertedPlanMiddle)
                        )
                    )
                }
                if(userPlans[i].toPlan().endTime() != newPlan.endTime()) {
                    val insertedPlan = Plan(
                        fromHour = newPlan.toHour,
                        fromMinute = newPlan.toMinute,
                        toHour = userPlans[i].toHour,
                        toMinute = userPlans[i].toMinute
                    )
                    val existingOneTimePlan = oneTimePlanDataSource.getOneTimePlanForUserOnDay(
                        userId = userId,
                        specificDay = specificDay
                    )
                    if(existingOneTimePlan != null) {
                        oneTimePlanDataSource.addPlanToOneTimePlan(
                            userId = userId,
                            specificDay = specificDay,
                            plan = insertedPlan
                        )
                    } else {
                        oneTimePlanDataSource.insertOneTimePlan(
                            OneTimePlan(
                                specificDay = specificDay,
                                userId = userId,
                                plans = listOf(insertedPlan)
                            )
                        )
                    }
                }
            }
        }
    }
}