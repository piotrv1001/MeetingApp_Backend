package com.vassev.domain.data_source

import com.vassev.domain.model.Meeting

interface MeetingDataSource {

    suspend fun getMeetingById(meetingId: String): Meeting?

    suspend fun insertMeeting(meeting: Meeting): Boolean

    suspend fun getAllMeetingsByUserId(userId: String): List<Meeting>

    suspend fun updateMeeting(meeting: Meeting): Boolean
}