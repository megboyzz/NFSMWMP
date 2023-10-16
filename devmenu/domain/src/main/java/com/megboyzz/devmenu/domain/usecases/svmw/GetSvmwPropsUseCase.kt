package com.megboyzz.devmenu.domain.usecases.svmw

import com.megboyzz.devmenu.domain.repository.SvmwRepository
import javax.inject.Inject

class GetSvmwPropsUseCase @Inject constructor(
    private val svmwRepository: SvmwRepository
) {
    suspend operator fun invoke() = svmwRepository.getInfoAboutLoadedSvmw()
}