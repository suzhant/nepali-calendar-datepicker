package com.example.android_nepali_calendar_picker_lib.model

data class MyDate(
    var id: String ?= null,
    var year: String,
    var month: String,
    var day : String,
    var isEnabled : Boolean = true
)