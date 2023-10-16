package com.megboyzz.devmenu.domain.usecases.files

import com.megboyzz.devmenu.domain.repository.FilesRepository
import javax.inject.Inject

class RemoveInternalDataUseCase @Inject constructor(
    private val filesRepository: FilesRepository
) {
    operator fun invoke(): Boolean{
        val listFiles = filesRepository.internalRoot.listFiles() ?: return false
        listFiles.forEach { it.deleteRecursively() }
        return true
    }
}