package com.example.playlistmaker.ui

import android.content.Context

interface ResourceProvider {
    fun getString(id: Int): String
}
class ResourceProviderImpl(private val context: Context) : ResourceProvider {

    override fun getString(id: Int): String {
        return  context.getString(id)
    }

}