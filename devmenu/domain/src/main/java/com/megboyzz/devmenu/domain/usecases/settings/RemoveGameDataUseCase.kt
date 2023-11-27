package com.megboyzz.devmenu.domain.usecases.settings

import com.megboyzz.devmenu.domain.usecases.files.GetInternalFilesUseCase
import javax.inject.Inject

class RemoveGameDataUseCase @Inject constructor(
    private val getInternalFilesUseCase: GetInternalFilesUseCase
) {
    operator fun invoke() = getInternalFilesUseCase().listFiles()?.forEach { it.delete() }
}