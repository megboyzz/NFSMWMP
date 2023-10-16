package com.megboyzz.devmenu.domain.usecases.replacement

import com.megboyzz.devmenu.domain.repository.ReplacementRepository
import javax.inject.Inject

class GetAllReplacedFilesUseCase @Inject constructor(
    private val replacementRepository: ReplacementRepository
) {
    suspend operator fun invoke() = replacementRepository.getAllReplacements()
}