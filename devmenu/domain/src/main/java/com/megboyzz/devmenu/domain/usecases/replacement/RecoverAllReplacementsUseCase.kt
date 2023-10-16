package com.megboyzz.devmenu.domain.usecases.replacement

import javax.inject.Inject

class RecoverAllReplacementsUseCase @Inject constructor(
    private val getAllReplacedFilesUseCase: GetAllReplacedFilesUseCase,
    private val recoverReplacementUseCase: RecoverReplacementUseCase
) {
    suspend operator fun invoke(){
        val listReplacedFiles = getAllReplacedFilesUseCase()
        listReplacedFiles.forEach { recoverReplacementUseCase(it) }
    }
}