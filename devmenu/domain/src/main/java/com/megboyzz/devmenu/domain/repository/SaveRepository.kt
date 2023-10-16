package com.megboyzz.devmenu.domain.repository

import java.io.File

interface SaveRepository {

    suspend fun loadSaveFile(file: File): Boolean

    suspend fun uploadCurrentSaveFile(fileToUpload: File): Boolean

}