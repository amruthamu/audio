package com.example.musicrecord.model

import android.content.Context
import android.os.Environment
import java.io.File

class AudioRepository(private val context: Context) {

    fun getOutputFilePath(fileName: String): String {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        return "${storageDir?.absolutePath}/$fileName"
    }
}
