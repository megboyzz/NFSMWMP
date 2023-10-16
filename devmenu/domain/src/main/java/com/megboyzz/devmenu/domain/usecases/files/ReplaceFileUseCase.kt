package com.megboyzz.devmenu.domain.usecases.files

import com.megboyzz.devmenu.domain.repository.ReplacementRepository
import java.io.File
import javax.inject.Inject

class ReplaceFileUseCase @Inject constructor(
    private val replacementRepository: ReplacementRepository
) {

    suspend operator fun invoke(what: File, to: File): Result<Boolean>
        = replacementRepository.replaceFile(what, to)

}