package com.vassev.domain.data_source

import com.vassev.domain.model.User

interface UserDataSource {

    suspend fun getAllUsers(): List<User>

    suspend fun getUserById(userId: String): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun insertUser(user: User): Boolean

    suspend fun getAllUsersForMeeting(userIds: List<String>): List<User>

    suspend fun updateUsersWithMeeting(userIds: List<String>, meetingId: String): Boolean
}