package com.megboyzz.devmenu.domain.usecases.settings

import com.megboyzz.devmenu.domain.entities.devmenuId
import com.megboyzz.devmenu.domain.repository.FilesRepository
import java.io.File
import javax.inject.Inject

class TurnOffDevMenuUseCase @Inject constructor(
    private val filesRepository: FilesRepository
) {
    operator fun invoke() = File(filesRepository.externalRoot, devmenuId).delete()
}