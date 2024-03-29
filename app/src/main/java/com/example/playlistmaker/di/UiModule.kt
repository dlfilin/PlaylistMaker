package com.example.playlistmaker.di

import com.example.playlistmaker.util.ResourceProvider
import com.example.playlistmaker.util.ResourceProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val UiModule = module {

    single<ResourceProvider> {
        ResourceProviderImpl(androidContext())
    }

}