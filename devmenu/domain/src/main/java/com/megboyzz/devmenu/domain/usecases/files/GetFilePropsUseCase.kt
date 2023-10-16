package com.megboyzz.devmenu.domain.usecases.files

import com.megboyzz.devmenu.domain.entities.FileProps
import com.megboyzz.devmenu.domain.repository.FilesRepository
import java.io.File
import javax.inject.Inject

class GetFilePropsUseCase @Inject constructor(
    private val filesRepository: FilesRepository
) {
    operator fun invoke(file: File): FileProps = filesRepository.getFileProps(file)
}