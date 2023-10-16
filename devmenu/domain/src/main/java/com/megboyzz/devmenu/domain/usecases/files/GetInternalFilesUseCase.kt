package com.megboyzz.devmenu.domain.usecases.files

import com.megboyzz.devmenu.domain.repository.FilesRepository
import java.io.File
import javax.inject.Inject

class GetInternalFilesUseCase @Inject constructor(
    private val filesRepository: FilesRepository
) {

    operator fun invoke(childPath: String = ""): File = File(filesRepository.internalRoot, childPath)

}