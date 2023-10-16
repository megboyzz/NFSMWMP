package com.ea.easp

import android.util.Log

//Класс вызывается из нативного кода
class ContactsAndroid {

    fun CanSendMail() = false

    fun CanSendSMS() = false

    fun OpenEmailClient(str: String?, str2: String?, str3: String?) = Log.i("ContactsAndroid", "OpenEmailClient")

    fun OpenSMSClient(str: String?, str2: String?) = Log.i("ContactsAndroid", "OpenSMSClient")

    val contacts = ""
}
