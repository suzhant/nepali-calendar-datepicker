package com.example.android_nepali_calendar_picker_lib.model

data class Month(
    val year: Int,
    val noOfDays: Int,
    val firstDayOfMonth : Int,
    val listOfDays: List<MyDate>
)
