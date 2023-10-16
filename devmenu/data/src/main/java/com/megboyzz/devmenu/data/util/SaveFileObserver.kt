package com.megboyzz.devmenu.data.util

import android.os.FileObserver
import android.util.Log
import java.io.File


class SaveFileObserver(
    private val source: File,
    private val destinationFolder: File
) : FileObserver(source.absolutePath) {

    override fun onEvent(event: Int, path: String?) {
        if (event and CREATE != 0 || event and MODIFY != 0) {
            Log.d("FileObserver", "File created or modified: $source")
            source copyToThe destinationFolder
        }
    }

    private infix fun File.copyToThe(destination: File) = this.copyTo(destination)
}

