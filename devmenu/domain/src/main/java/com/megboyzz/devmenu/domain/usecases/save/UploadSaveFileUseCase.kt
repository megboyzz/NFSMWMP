package com.megboyzz.devmenu.domain.usecases.save

import com.megboyzz.devmenu.domain.repository.SaveRepository
import java.io.File
import javax.inject.Inject

class UploadSaveFileUseCase @Inject constructor(
    private val saveRepository: SaveRepository
) {
    suspend operator fun invoke(fileToUpload: File) =
        if(fileToUpload.exists())
            saveRepository.uploadCurrentSaveFile(fileToUpload)
        else
            false
}