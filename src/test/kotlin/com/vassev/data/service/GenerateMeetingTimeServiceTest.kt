package com.vassev.data.service

import com.google.common.truth.Truth.assertThat
import com.vassev.data.data_source.FakeOneTimePlanDataSource
import com.vassev.data.data_source.FakeRepeatedPlanDataSource
import com.vassev.data.requests.GenerateTimeRequest
import com.vassev.domain.model.OneTimePlan
import com.vassev.domain.model.Plan
import com.vassev.domain.model.RepeatedPlan
import com.vassev.domain.model.SpecificDay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GenerateMeetingTimeServiceTest {

    private lateinit var generateMeetingTimeService: GenerateMeetingTimeServiceImpl
    private lateinit var fakeOneTimePlanDataSource: FakeOneTimePlanDataSource
    private lateinit var fakeRepeatedPlanDataSource: FakeRepeatedPlanDataSource
    private lateinit var userIds: List<String>
    private lateinit var today: SpecificDay
    private lateinit var generateTimeRequest: GenerateTimeRequest
    private var duration: Int = 30
    private var numberOfWeeks: Int = 1
    private var numberOfResults: Int = 3

    @Before
    fun setUp() {
        fakeOneTimePlanDataSource = FakeOneTimePlanDataSource()
        fakeRepeatedPlanDataSource = FakeRepeatedPlanDataSource()
        generateMeetingTimeService = GenerateMeetingTimeServiceImpl(
            repeatedPlanDataSource = fakeRepeatedPlanDataSource,
            oneTimePlanDataSource = fakeOneTimePlanDataSource
        )
        userIds = (1..5).map { getRandomString(5)}
        val todayTime = LocalDate.now()
        today = SpecificDay(
            day = todayTime.dayOfMonth,
            month = todayTime.monthValue,
            year = todayTime.year
        )
        generateTimeRequest = GenerateTimeRequest(
            numberOfWeeks = numberOfWeeks,
            numberOfResults = numberOfResults,
            preferredTime = 0
        )
        getFakeOneTimePlanData()
        getFakeRepeatedPlanData()
    }

    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun getFakeOneTimePlanData() {
        var date = LocalDate.of(today.year, today.month, today.day)
        for(i in 1..7 * numberOfWeeks) {
            date = date.plusDays(1)
            for(user in userIds) {
                val startTime = (6..12).random()
                val endTime = (13..22).random()
                val plan = Plan(
                    fromHour = startTime,
                    fromMinute = 0,
                    toHour = endTime,
                    toMinute = 0
                )
                val oneTimePlan = OneTimePlan(
                    specificDay = SpecificDay(date.dayOfMonth, date.monthValue, date.year),
                    userId = user,
                    plans = listOf(plan)
                )
                runBlocking {
                    fakeOneTimePlanDataSource.insertOneTimePlan(oneTimePlan)
                }
            }
        }
    }

    private fun getFakeRepeatedPlanData() {
        for(i in 1..7) {
            for(user in userIds) {
                val startTime = (6..12).random()
                val endTime = (13..22).random()
                val plan = Plan(
                    fromHour = startTime,
                    fromMinute = 0,
                    toHour = endTime,
                    toMinute = 0
                )
                val repeatedPlan = RepeatedPlan(
                    dayOfWeek = i,
                    userId = user,
                    plans = listOf(plan)
                )
                runBlocking {
                    fakeRepeatedPlanDataSource.insertRepeatedPlan(repeatedPlan)
                }
            }
        }
    }

    private fun getFakeRepeatedPlanDataWithExceptions() {
        var date = LocalDate.of(today.year, today.month, today.day)
        for(i in 1..7) {
            date = date.plusDays(1)
            for(user in userIds) {
                val startTime = (6..12).random()
                val endTime = (13..22).random()
                val plan = Plan(
                    fromHour = startTime,
                    fromMinute = 0,
                    toHour = endTime,
                    toMinute = 0
                )
                if(i % 2 == 0) {
                    runBlocking {
                        val repeatedPlan = RepeatedPlan(
                            dayOfWeek = i,
                            userId = user,
                            plans = listOf(plan)
                        )
                        fakeRepeatedPlanDataSource.insertRepeatedPlan(repeatedPlan)
                    }
                } else {
                    runBlocking {
                        val currentSpecificDay = SpecificDay(date.dayOfMonth, date.monthValue, date.year)
                        val repeatedPlan = RepeatedPlan(
                            dayOfWeek = i,
                            userId = user,
                            plans = listOf(plan),
                            except = listOf(currentSpecificDay)
                        )
                        fakeRepeatedPlanDataSource.insertRepeatedPlan(repeatedPlan)
                    }
                }
            }
        }
    }

    @Test
    fun `Meeting time generation algorithm, one-time plans`() = runBlocking {
        val resultList = generateMeetingTimeService.generateMeetingTime(
            today = today,
            userIds = userIds,
            duration = duration,
            generateTimeRequest = generateTimeRequest
        )
        for(result in resultList) {
            assertThat(result.plan.isWithinRange(duration))
            for(user in userIds) {
                var date = LocalDate.of(today.year, today.month, today.day)
                for(i in 1..7 * numberOfWeeks) {
                    date = date.plusDays(1)
                    val currentSpecificDay = SpecificDay(date.dayOfMonth, date.monthValue, date.year)
                    val userOneTimePlans = fakeOneTimePlanDataSource.getOneTimePlanForUserOnDay(user, currentSpecificDay)
                    for(plan in userOneTimePlans?.plans!!) {
                        assertThat(plan.isWithinAnotherPlan(result.plan))
                    }
                }
            }
        }
    }

    @Test
    fun `Meeting time generation algorithm, repeated plans`() = runBlocking {
        val resultList = generateMeetingTimeService.generateMeetingTime(
            today = today,
            userIds = userIds,
            duration = duration,
            generateTimeRequest = generateTimeRequest
        )
        for(result in resultList) {
            assertThat(result.plan.isWithinRange(duration))
            for(user in userIds) {
                var date = LocalDate.of(today.year, today.month, today.day)
                for(i in 1..7 * numberOfWeeks) {
                    date = date.plusDays(1)
                    val currentSpecificDay = SpecificDay(date.dayOfMonth, date.monthValue, date.year)
                    val currentDayOfWeek = date.dayOfWeek.value
                    val userOneTimePlans = fakeOneTimePlanDataSource.getOneTimePlanForUserOnDay(user, currentSpecificDay)
                    val userRepeatedPlans = fakeRepeatedPlanDataSource.getRepeatedPlanForUser(user, currentDayOfWeek)
                    for(plan in userRepeatedPlans?.plans!!) {
                        assertThat(plan.isWithinAnotherPlan(result.plan))
                    }
                }
            }
        }
    }

    @Test
    fun `Meeting time generation algorithm, mixed plans`() = runBlocking {
        val resultList = generateMeetingTimeService.generateMeetingTime(
            today = today,
            userIds = userIds,
            duration = duration,
            generateTimeRequest = generateTimeRequest
        )
        for(result in resultList) {
            assertThat(result.plan.isWithinRange(duration))
            for(user in userIds) {
                var date = LocalDate.of(today.year, today.month, today.day)
                for(i in 1..7 * numberOfWeeks) {
                    date = date.plusDays(1)
                    val currentSpecificDay = SpecificDay(date.dayOfMonth, date.monthValue, date.year)
                    val currentDayOfWeek = date.dayOfWeek.value
                    val userOneTimePlans = fakeOneTimePlanDataSource.getOneTimePlanForUserOnDay(user, currentSpecificDay)
                    val userRepeatedPlans = fakeRepeatedPlanDataSource.getRepeatedPlanForUser(user, currentDayOfWeek)
                    for(plan in userOneTimePlans?.plans!!) {
                        assertThat(plan.isWithinAnotherPlan(result.plan))
                    }
                    for(plan in userRepeatedPlans?.plans!!) {
                        assertThat(plan.isWithinAnotherPlan(result.plan))
                    }
                }
            }
        }
    }

}