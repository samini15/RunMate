package com.example.auth.presentation.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import com.example.auth.presentation.register.RegisterViewModel
import com.example.auth.presentation.login.LoginViewModel
import org.koin.dsl.module

val authViewModelModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::LoginViewModel)
}