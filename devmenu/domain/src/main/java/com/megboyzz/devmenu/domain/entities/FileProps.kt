package com.megboyzz.devmenu.domain.entities

import java.util.Date

data class FileProps(
    val fileName: String,
    val size: Int,
    val dateOfCreation: Date
)
