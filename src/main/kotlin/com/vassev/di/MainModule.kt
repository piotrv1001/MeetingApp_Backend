package com.vassev.di

import com.vassev.chat_room.RoomController
import com.vassev.data.data_source.*
import com.vassev.data.service.GenerateMeetingTimeServiceImpl
import com.vassev.domain.data_source.*
import com.vassev.domain.service.GenerateMeetingTimeService
import com.vassev.security.token.JwtTokenService
import com.vassev.security.token.TokenService
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        val mongoPassword = System.getenv("MONGO_PASSWORD")
        val dbName = "ktor-meeting"
        KMongo.createClient(
            connectionString = "mongodb+srv://piotrv1001:$mongoPassword@cluster0.6bwasqg.mongodb.net/$dbName?retryWrites=true&w=majority"
        )
            .coroutine
            .getDatabase(dbName)
    }
    single<UserDataSource> {
        UserDataSourceImpl(
            db = get()
        )
    }
    single<MeetingDataSource> {
        MeetingDataSourceImpl(
            db = get()
        )
    }
    single<MessageDataSource> {
        MessageDataSourceImpl(
            db = get()
        )
    }
    single<OneTimePlanDataSource> {
        OneTimePlanDataSourceImpl(
            db = get()
        )
    }
    single<RepeatedPlanDataSource> {
        RepeatedPlanDataSourceImpl(
            db = get()
        )
    }
    single<TokenService> {
        JwtTokenService()
    }
    single<RoomController> {
        RoomController(
            messageDataSource = get()
        )
    }
    single<GenerateMeetingTimeService> {
        GenerateMeetingTimeServiceImpl(
            repeatedPlanDataSource = get(),
            oneTimePlanDataSource = get()
        )
    }
}