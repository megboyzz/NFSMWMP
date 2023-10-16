package com.megboyzz.devmenu.domain.repository

import com.megboyzz.devmenu.domain.entities.SvmwProps
import java.io.File

interface SvmwRepository {

    suspend fun createSvmwFromCurrentSaveFileAndSetIt(): Boolean

    suspend fun createSvmwFromCurrentSaveFile(fileToSave: File): Boolean

    suspend fun createSvmw(fileToSave: File, saveFile: File): Boolean

    suspend fun loadSvmw(svmw: File): Boolean

    suspend fun getInfoAboutSvmw(svmw: File): SvmwProps?

    suspend fun getInfoAboutLoadedSvmw(): SvmwProps?

}