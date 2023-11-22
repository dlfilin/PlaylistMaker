package com.example.playlistmaker.util

import android.content.Context

interface ResourceProvider {
    fun getString(id: Int): String

    fun getQuantityString(id: Int, qty: Int): String

}

class ResourceProviderImpl(private val context: Context) : ResourceProvider {

    override fun getString(id: Int): String {
        return context.getString(id)
    }

    override fun getQuantityString(id: Int, qty: Int): String {
        return context.resources.getQuantityString(id, qty, qty)
    }

}