package com.example.playlistmaker.util

sealed class Resource<T>(val data: T? = null, val code: Int? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(code: Int, data: T? = null): Resource<T>(data, code)
}