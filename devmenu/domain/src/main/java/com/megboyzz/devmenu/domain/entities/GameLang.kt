package com.megboyzz.devmenu.domain.entities

enum class GameLang(private val lang: String) {
    System("sys"),
    Chinese("cn"),
    Dutch("nl"),
    English("en"),
    French("fr"),
    Deutsch("de"),
    Italian("it"),
    Japanese("ja"),
    Korean("kr"),
    Portuguese("br"),
    Russian("ru"),
    Spanish("es");

    override fun toString() = lang

}