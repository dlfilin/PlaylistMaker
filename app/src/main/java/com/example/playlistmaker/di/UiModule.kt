package com.example.playlistmaker.di

import com.example.playlistmaker.ui.ResourceProvider
import com.example.playlistmaker.ui.ResourceProviderImpl
import org.koin.dsl.module

val UiModule = module {

    single<ResourceProvider> {
        ResourceProviderImpl(get())
    }

}