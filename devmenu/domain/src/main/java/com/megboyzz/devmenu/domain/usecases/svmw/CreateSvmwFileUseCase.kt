package com.megboyzz.devmenu.domain.usecases.svmw

import com.megboyzz.devmenu.domain.repository.SvmwRepository
import java.io.File
import javax.inject.Inject

class CreateSvmwFileUseCase @Inject constructor(
    private val svmwRepository: SvmwRepository
) {

    suspend operator fun invoke(
        fileToSave: File? = null,
        saveFile: File? = null
    ){

        val a = fileToSave != null
        val b = saveFile != null

        when {
            a && b -> svmwRepository.createSvmw(fileToSave!!, saveFile!!)
            a && !b -> svmwRepository.createSvmwFromCurrentSaveFile(fileToSave!!)
            !a && !b -> svmwRepository.createSvmwFromCurrentSaveFileAndSetIt()
        }

    }

}