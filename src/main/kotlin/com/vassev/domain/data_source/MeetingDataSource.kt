package com.vassev.domain.data_source

import com.vassev.domain.model.Meeting

interface MeetingDataSource {

    suspend fun getMeetingById(meetingId: String): Meeting?

    suspend fun getAllMeetings(): List<Meeting>

    suspend fun insertMeeting(meeting: Meeting): Meeting

    suspend fun getAllMeetingsForUser(meetingIds: List<String>): List<Meeting>

    suspend fun updateMeeting(meeting: Meeting): Boolean

    suspend fun updateMeetingDate(meetingId: String, date: String): Meeting?
}