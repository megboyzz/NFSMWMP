package com.megboyzz.devmenu.domain.usecases.hidefiles

import com.megboyzz.devmenu.domain.repository.FilesRepository
import java.io.File
import javax.inject.Inject

class UnHideFileUseCase @Inject constructor(
    private val filesRepository: FilesRepository
) {

    suspend operator fun invoke(file: File) = filesRepository.unHideFile(file)

}