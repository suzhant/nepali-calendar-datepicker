package com.example.android_nepali_calendar_picker_lib.model


data class MyCalendar(
    val year : Int,
    val month : List<Month>,
    val currentDateId: String,
    var selectedDateId: String,
    var themeColor: Int
)
