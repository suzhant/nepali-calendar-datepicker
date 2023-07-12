package com.example.android_nepali_calendar_picker_lib.extensions

import kotlin.text.StringBuilder


internal fun StringBuilder.countSlashOccurrences(): Int {
    return this.count { it == '/' }
}

internal fun String.removeSlash(): String {
    return this.replace("/", "")
}

internal fun StringBuilder.insertSlashAtPositions(){
    if (this.length >= 3 && this[2] != '/'){
        this.insert(2, '/')
    }

    if (this.length >= 6 && this[5] != '/') {
        this.insert(5, '/')
    }
}

internal fun String.removeLeadingZero(): String{
    return  this.trimStart('0')
}
