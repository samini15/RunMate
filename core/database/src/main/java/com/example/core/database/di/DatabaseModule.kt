package com.example.core.database.di

import androidx.room.Room
import com.example.core.database.RunDatabase
import com.example.core.domain.run.LocalRunDataSource
import com.example.core.database.RoomLocalRunDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            RunDatabase::class.java,
            name = "run_database"
        ).build()
    }

    single { get<RunDatabase>().runDao }

    singleOf(::RoomLocalRunDataSource).bind<LocalRunDataSource>()
}