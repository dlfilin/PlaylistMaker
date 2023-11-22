package com.example.playlistmaker.data.storage

import android.net.Uri

interface ImagesStorage {

    suspend fun saveImageToPrivateStorage(sharedPath: Uri, album: String): Uri?

    suspend fun deleteImageFromPrivateStorage(uri: Uri, album: String): Boolean

}