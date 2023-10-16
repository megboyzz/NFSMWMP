package com.megboyzz.devmenu.domain.repository

import java.io.File

interface ReplacementRepository {

    suspend fun replaceFile(what: File, to: File): Result<Boolean>

    suspend fun recoverReplacement(replacement: File): Result<Boolean>

    suspend fun getAllReplacements(): List<File>

}