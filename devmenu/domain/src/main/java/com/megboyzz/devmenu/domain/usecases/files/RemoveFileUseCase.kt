package com.megboyzz.devmenu.domain.usecases.files

import java.io.File

class RemoveFileUseCase {
    operator fun invoke(file: File): Boolean = file.delete()
}