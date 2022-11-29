package com.vassev.data.service

import com.google.common.truth.Truth.assertThat
import com.vassev.data.data_source.FakeOneTimePlanDataSource
import com.vassev.data.data_source.FakeRepeatedPlanDataSource
import com.vassev.data.requests.GenerateTimeRequest
import com.vassev.domain.model.OneTimePlan
import com.vassev.domain.model.Plan
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
        populateTestData()
    }

    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun populateTestData() {
        var date = LocalDate.of(today.year, today.month, today.day)
        for(i in 1..7 * numberOfWeeks) {
            date = date.plusDays(1)
            println(date)
            for(user in userIds) {
                val startTime = (6..12).random()
                val endTime = (13..22).random()
                println("User: $user, $startTime:00 - $endTime:00")
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
            println()
        }
    }

    @Test
    fun `Meeting time generation algorithm, correct result`() = runBlocking {
        val resultList = generateMeetingTimeService.generateMeetingTime(
            today = today,
            userIds = userIds,
            duration = duration,
            generateTimeRequest = generateTimeRequest
        )
        println("--- RESULTS ---")
        for(result in resultList) {
            println("${result.specificDay.year}-${result.specificDay.month}-${result.specificDay.day}")
            println("${result.plan.fromHour}:${result.plan.fromMinute} - ${result.plan.toHour}:${result.plan.toMinute}")
            assertThat(result.plan.isWithinRange(duration))
            for(user in userIds) {
                var date = LocalDate.of(today.year, today.month, today.day)
                for(i in 1..7 * numberOfWeeks) {
                    date = date.plusDays(1)
                    val currentSpecificDay = SpecificDay(date.dayOfMonth, date.monthValue, date.year)
//                    val currentDayOfWeek = date.dayOfWeek.value
                    val userOneTimePlans = fakeOneTimePlanDataSource.getOneTimePlanForUserOnDay(user, currentSpecificDay)
                    for(plan in userOneTimePlans?.plans!!) {
                        assertThat(plan.isWithinAnotherPlan(result.plan))
                    }
                }
            }
        }
    }

}