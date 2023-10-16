package com.megboyzz.devmenu.domain.usecases.settings

import com.megboyzz.devmenu.domain.entities.Settings
import com.megboyzz.devmenu.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Settings = settingsRepository.getSettings()
}