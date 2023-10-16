package com.megboyzz.devmenu.domain.repository

import com.megboyzz.devmenu.domain.entities.Settings

interface SettingsRepository {

    suspend fun getSettings(): Settings

    suspend fun setSettings(settings: Settings)

}