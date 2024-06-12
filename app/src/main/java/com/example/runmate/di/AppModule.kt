package com.example.runmate.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.example.runmate.MainViewModel
import androidx.security.crypto.MasterKey
import com.example.runmate.RunmateApplication
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single<SharedPreferences> {
        EncryptedSharedPreferences(
            androidApplication(),
            "auth_pref",
            MasterKey(androidApplication()),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    single<CoroutineScope> {
        (androidApplication() as RunmateApplication).applicationScope
    }

    viewModelOf(::MainViewModel)
}