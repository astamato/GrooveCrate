package com.example.myapplication.di

import com.example.myapplication.data.AuthManager
import com.example.myapplication.data.DiscogsRepository
import com.example.myapplication.data.RecordIdentifier
import com.example.myapplication.ui.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { AuthManager(androidContext()) }
    single { DiscogsRepository(get()) }
    single { RecordIdentifier() }
    viewModelOf(::MainViewModel)
}
