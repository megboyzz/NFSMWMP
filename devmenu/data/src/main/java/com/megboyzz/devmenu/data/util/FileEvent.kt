package com.megboyzz.devmenu.data.util

import android.os.FileObserver

enum class FileEvent(val value: Int) {

    ACCESS(FileObserver.ACCESS),
    MODIFY(FileObserver.MODIFY),
    ATTRIB(FileObserver.ATTRIB),
    CLOSE_WRITE(FileObserver.CLOSE_WRITE),
    CLOSE_NOWRITE(FileObserver.CLOSE_NOWRITE),
    OPEN(FileObserver.OPEN),
    MOVED_FROM(FileObserver.MOVED_FROM),
    MOVED_TO(FileObserver.MOVED_TO),
    CREATE(FileObserver.CREATE),
    DELETE(FileObserver.DELETE),
    DELETE_SELF(FileObserver.DELETE_SELF),
    MOVE_SELF(FileObserver.MOVE_SELF),
    UNKNOWN(-1);

    override fun toString() = this.name
}