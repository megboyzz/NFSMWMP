package com.megboyzz.devmenu.domain.usecases.svmw

import com.megboyzz.devmenu.domain.repository.SvmwRepository
import java.io.File
import javax.inject.Inject

class LoadSvmwUseCase @Inject constructor(
    private val svmwRepository: SvmwRepository
) {
    suspend operator fun invoke(file: File) = if(file.exists()) svmwRepository.loadSvmw(file) else false
}