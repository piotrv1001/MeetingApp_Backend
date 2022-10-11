package com.vassev.data.data_source

import com.vassev.domain.data_source.MeetingDataSource
import com.vassev.domain.model.Meeting
import org.litote.kmongo.ascending
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.match
import org.litote.kmongo.sort

class MeetingDataSourceImpl(
    db: CoroutineDatabase
): MeetingDataSource {

    private val meetings = db.getCollection<Meeting>()

    override suspend fun getMeetingById(meetingId: String): Meeting? {
        return meetings.findOneById(meetingId)
    }

    override suspend fun insertMeeting(meeting: Meeting): Boolean {
        return meetings.insertOne(meeting).wasAcknowledged()
    }

    override suspend fun getAllMeetingsByUserId(userId: String): List<Meeting> {
        return meetings.find(Meeting::users contains userId).toList()
    }
}