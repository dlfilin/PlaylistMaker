package com.example.playlistmaker.data.storage.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.example.playlistmaker.data.storage.ImagesStorage
import java.io.File
import java.io.FileOutputStream

class ImagesStorageImpl(
    private val context: Context,
) : ImagesStorage {

    override suspend fun saveImageToPrivateStorage(sharedPath: Uri, album: String): Uri? {

        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), album)
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //имя файла - текущее время
        val file = File(filePath, System.currentTimeMillis().toString() + ".jpg")

        try {
            // создаём входящий поток байтов из выбранной картинки
            val inputStream = context.contentResolver.openInputStream(sharedPath)
            // создаём исходящий поток байтов в созданный выше файл
            val outputStream = FileOutputStream(file)
            // записываем картинку с помощью BitmapFactory
            BitmapFactory.decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        } catch (e: Exception) {
            return null
        }
        return file.toUri()
    }

    override suspend fun deleteImageFromPrivateStorage(uri: Uri, album: String): Boolean {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), album)
        val file = File(filePath, uri.lastPathSegment ?: "")
        return try {
            file.delete()
        } catch (e: Exception) {
            Log.d("deleteImageFromPrivateStorage", e.printStackTrace().toString())
            false
        }
    }

}