package com.megboyzz.devmenu.domain.usecases.hidefiles

import javax.inject.Inject

class UnHideAllUseCase @Inject constructor(
    private val unHideFileUseCase: UnHideFileUseCase,
    private val getAllHidedFilesUseCase: GetAllHidedFilesUseCase
) {

    suspend operator fun invoke() = getAllHidedFilesUseCase().forEach {
        unHideFileUseCase(it)
    }

}