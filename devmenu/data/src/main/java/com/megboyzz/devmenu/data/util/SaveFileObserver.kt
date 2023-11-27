package com.megboyzz.devmenu.data.util

import android.util.Log
import java.io.File
import kotlin.concurrent.thread


class SaveFileObserver(
    private val source: String,
    private val destinationFolder: String
) : BaseObserver(source) {

    private var countSnapshots: Int = 1

    private val checkList = listOf(
        FileEvent.CLOSE_WRITE,
        FileEvent.MODIFY,
        //FileEvent.DELETE,
        FileEvent.CREATE,
        //FileEvent.ACCESS,
        //FileEvent.OPEN,
        FileEvent.CLOSE_NOWRITE
        )
    init {
        val dest = File(destinationFolder)
        if(dest.isFile) dest.delete()
        if(!dest.exists()) dest.mkdir()
        val list = dest.list{
            dir, name -> name.endsWith(".sb")
        }
        list?.forEach { File(dest, it).delete() }
        File(source) copyToThe File(destinationFolder, "BASE.sb")
    }

    override fun onEvent(event: FileEvent, path: String?) {
        Log.i("SaveFileObserver", "onEvent $event")
        if (event in checkList) {
            Log.d("SaveFileObserver", "File created or modified: $source")
            File(source) copyToThe File(destinationFolder, "$event-$countSnapshots.sb")
            countSnapshots++
        }
    }

    override fun stopWatching() {
        super.stopWatching()
        val filesList = File(destinationFolder).list{ _, name -> name.endsWith(".sb") }?.map { it }
        if (filesList != null) {
            zipAll(destinationFolder, filesList)
        }
    }
}

