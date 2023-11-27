package com.megboyzz.devmenu.domain.usecases.hidefiles

import com.megboyzz.devmenu.domain.repository.FilesRepository
import javax.inject.Inject

class GetAllHidedFilesUseCase @Inject constructor(
    private val filesRepository: FilesRepository
) {

    operator fun invoke() = filesRepository.allHidedFiles

}