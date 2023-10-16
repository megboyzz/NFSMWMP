package com.megboyzz.devmenu.domain.usecases.replacement

import com.megboyzz.devmenu.domain.repository.ReplacementRepository
import java.io.File
import javax.inject.Inject

class RecoverReplacementUseCase @Inject constructor(
    private val replacementRepository: ReplacementRepository
) {
    suspend operator fun invoke(replacement: File) = replacementRepository.recoverReplacement(replacement)
}