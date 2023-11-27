package com.megboyzz.devmenu.domain.repository

import com.megboyzz.devmenu.domain.entities.FileProps
import java.io.File

interface FilesRepository {

    val internalRoot: File

    val externalRoot: File

    fun getFileProps(file: File): FileProps

    suspend fun hideFile(file: File): Boolean

    suspend fun unHideFile(file: File): Boolean

    val allHidedFiles: List<File>

}