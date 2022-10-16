package com.vassev.data.data_source

import com.vassev.domain.data_source.UserDataSource
import com.vassev.domain.model.User
import org.litote.kmongo.addToSet
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class UserDataSourceImpl(
    db: CoroutineDatabase
): UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun getAllUsersByMeetingId(meetingId: String): List<User> {
        return users.find(User::meetings contains meetingId).toList()
    }

    override suspend fun getAllUsers(): List<User> {
        return users.find().toList()
    }

    override suspend fun getUserById(userId: String): User? {
        return users.findOneById(userId)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq email)
    }

    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun updateUsersWithMeeting(userIds: List<String>, meetingId: String): Boolean {
        return users.updateMany(User::userId `in` userIds, addToSet(User::meetings, meetingId)).wasAcknowledged()
    }
}