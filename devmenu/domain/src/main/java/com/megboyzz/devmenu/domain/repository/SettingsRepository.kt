package com.megboyzz.devmenu.domain.repository

import com.megboyzz.devmenu.domain.entities.GameLang

interface SettingsRepository {

    fun setGameLang(lang: GameLang): Boolean


}