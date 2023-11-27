package com.megboyzz.devmenu.domain.usecases.files

import java.io.File

class RenameFileUseCase {
    operator fun invoke(file: File, newName: String) =
        if(file.exists())
            file.renameTo(File(file.parent, newName))
        else
            false
}