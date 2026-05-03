package com.example.myapplication.di

import com.example.myapplication.data.DiscogsRepository
import com.example.myapplication.data.RecordIdentifier
import com.example.myapplication.ui.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { DiscogsRepository() }
    single { RecordIdentifier() }
    viewModelOf(::MainViewModel)
}
