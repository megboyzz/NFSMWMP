package com.megboyzz.devmenu.domain.repository

interface TrackingRepository {

    fun setTracking(enabled: Boolean, pathToSave: String): Boolean

}