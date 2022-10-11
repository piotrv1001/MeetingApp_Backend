package com.vassev.domain.data_source

import com.vassev.domain.model.User

interface UserDataSource {

    suspend fun getAllUsers(): List<User>

    suspend fun getUserById(userId: String): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun insertUser(user: User): Boolean

    suspend fun getAllUsersByMeetingId(meetingId: String): List<User>
}