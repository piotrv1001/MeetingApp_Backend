package com.vassev.data.data_source

import com.vassev.domain.data_source.MeetingDataSource
import com.vassev.domain.model.Meeting
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.updateOne

class MeetingDataSourceImpl(
    db: CoroutineDatabase
): MeetingDataSource {

    private val meetings = db.getCollection<Meeting>()

    override suspend fun getAllMeetings(): List<Meeting> {
        return meetings.find().toList()
    }

    override suspend fun getMeetingById(meetingId: String): Meeting? {
        return meetings.findOne(Meeting::meetingId eq meetingId)
    }

    override suspend fun insertMeeting(meeting: Meeting): Meeting {
        return meeting.apply { meetings.insertOne(meeting) }
    }

    override suspend fun getAllMeetingsForUser(meetingIds: List<String>): List<Meeting> {
        return meetings.find(Meeting::meetingId `in` meetingIds).toList()
    }

    override suspend fun updateMeeting(meeting: Meeting): Boolean {
        return meetings.updateOne(Meeting::meetingId eq meeting.meetingId, meeting).wasAcknowledged()
    }

    override suspend fun updateMeetingDate(meetingId: String, date: String): Meeting? {
        return meetings.findOneAndUpdate(Meeting::meetingId eq meetingId, setValue(Meeting::date, date))
    }
}