package com.megboyzz.devmenu.data.util

import android.os.FileObserver
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.LinkedList
import java.util.Queue
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

abstract class BaseObserver(source: String) : FileObserver(source) {

    private val eventQueue : Queue<Int> = LinkedList()

    @OptIn(ExperimentalStdlibApi::class)
    final override fun onEvent(event: Int, path: String?) {

        if(eventQueue.size < 3) eventQueue.add(event)

        val e = FileEvent.entries.find { it.value == event }

        val fileEvent = e ?: FileEvent.UNKNOWN

        Log.i("BaseObserver", "event code = ${event.toHexString()}, FileEvent is $fileEvent")

        if(hasLastThreeEvents()) onEvent(fileEvent, path)
    }

    private fun hasLastThreeEvents() = runCatching {
        eventQueue.size == 3 && eventQueue.poll() == OPEN && eventQueue.poll() == ACCESS && eventQueue.poll() == CLOSE_NOWRITE
    }.getOrNull() ?: false

    abstract fun onEvent(event: FileEvent, path: String?)

    protected fun File.list(regex: String = ".") =
        this.list { dir, _ -> Regex(regex).matches(dir.absolutePath) }?.asList() ?: listOf()


    protected infix fun File.copyToThe(destination: File) = this.copyTo(destination, overwrite = true)

    protected fun zipAll(outputFolder: String, filesToCompress: List<String>) {
        if(filesToCompress.isEmpty()){
            Log.i("BaseObserver", "no files exit!")
            return
        }
        try {
            // Проверяем, существует ли выходная папка. Если нет, создаем её.
            val outputDir = File(outputFolder)
            if (!outputDir.exists()) outputDir.mkdirs()

            // Генерируем имя ZIP-архива на основе времени
            val timestamp = System.currentTimeMillis()
            val zipFileName = "$outputFolder/saves_$timestamp.zip"
            val zipOutputStream = ZipOutputStream(FileOutputStream(zipFileName))

            for (fileToCompress in filesToCompress) {
                // Проверяем, существует ли файл
                val inputFile = File(fileToCompress)
                if (inputFile.exists()) {
                    // Создаем объект ZipEntry для каждого файла
                    val zipEntry = ZipEntry(inputFile.name)
                    zipOutputStream.putNextEntry(zipEntry)

                    // Читаем содержимое файла и записываем его в ZIP-архив
                    val fileInputStream = FileInputStream(inputFile)
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (fileInputStream.read(buffer).also { len = it } > 0) {
                        zipOutputStream.write(buffer, 0, len)
                    }
                    fileInputStream.close()

                    // Закрываем текущий ZipEntry
                    zipOutputStream.closeEntry()
                } else {
                    Log.i("SaveFileObserver", "Файл не существует: $fileToCompress")
                }
            }

            // Закрываем ZipOutputStream
            zipOutputStream.close()
            Log.i("SaveFileObserver", "Архивация завершена. Архив создан: $zipFileName")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}