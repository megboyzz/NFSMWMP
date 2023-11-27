package com.megboyzz.devmenu.domain.usecases.settings

import com.megboyzz.devmenu.domain.repository.TrackingRepository
import javax.inject.Inject

class SetTrackingSaveFileUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {

    operator fun invoke(enabled: Boolean, pathToSave: String) =
        trackingRepository.setTracking(enabled, pathToSave)

}