package com.megboyzz.devmenu.domain.entities

data class Settings(
    val enable: Boolean,
    val trackingInterval: Int,
    val pathToSaveTrackable: String,
    val lang: GameLang
)
