package com.megboyzz.devmenu.domain.usecases.settings

import com.megboyzz.devmenu.domain.entities.GameLang
import com.megboyzz.devmenu.domain.repository.SettingsRepository
import javax.inject.Inject

class SetGameLangUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(gameLang: GameLang) = settingsRepository.setGameLang(gameLang)

}