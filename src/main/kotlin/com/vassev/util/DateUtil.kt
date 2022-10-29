package com.vassev.util

import java.text.DateFormatSymbols

class DateUtil {

    companion object {

        fun getMonthName(month: Int): String {
            return DateFormatSymbols().months[month - 1]
        }

        fun getDayOfWeekName(dayOfWeek: Int): String {
            var weekDay = ""
            when(dayOfWeek) {
                1 -> {
                    weekDay = "Monday"
                }
                2 -> {
                    weekDay = "Tuesday"
                }
                3 -> {
                    weekDay = "Wednesday"
                }
                4 -> {
                    weekDay = "Thursday"
                }
                5 -> {
                    weekDay = "Friday"
                }
                6 -> {
                    weekDay = "Saturday"
                }
                7 -> {
                    weekDay = "Sunday"
                }
            }
            return weekDay
        }

        fun getFormattedPlan(fromHour: Int, fromMinute: Int, toHour: Int, toMinute: Int): String {
            var fromMinuteFormatted = fromMinute.toString()
            if(fromMinute < 10) {
                fromMinuteFormatted = "0$fromMinute"
            }
            var toMinuteFormatted = toMinute.toString()
            if(toMinute < 10) {
                toMinuteFormatted = "0$toMinute"
            }
            return "$fromHour:$fromMinuteFormatted - $toHour:$toMinuteFormatted"
        }
    }
}