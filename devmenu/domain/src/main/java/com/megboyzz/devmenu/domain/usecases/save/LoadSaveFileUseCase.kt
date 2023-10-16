package com.megboyzz.devmenu.domain.usecases.save

import com.megboyzz.devmenu.domain.repository.SaveRepository
import java.io.File
import javax.inject.Inject

class LoadSaveFileUseCase @Inject constructor(
    private val saveRepository: SaveRepository
) {
    suspend operator fun invoke(file: File) = if(file.exists()) saveRepository.loadSaveFile(file) else false
}