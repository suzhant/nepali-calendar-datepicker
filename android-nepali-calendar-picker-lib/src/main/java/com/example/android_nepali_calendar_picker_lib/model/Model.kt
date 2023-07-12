package com.example.android_nepali_calendar_picker_lib.model

import androidx.annotation.IntRange
import java.util.Calendar
import java.util.GregorianCalendar

class Model {
    var day: Int
    var year: Int
    var month: Int
    var dayOfWeek = 0

    constructor() {
        val date = GregorianCalendar()
        day = date[GregorianCalendar.DAY_OF_MONTH]
        month = date[GregorianCalendar.MONTH] + 1
        year = date[GregorianCalendar.YEAR]
        dayOfWeek = date[Calendar.DAY_OF_WEEK]
    }

    constructor(
        @IntRange(from = 1970, to = 2090) year: Int,
        @IntRange(from = 0, to = 12) month: Int,
        @IntRange(from = 1, to = 32) day: Int
    ) {
        this.year = year
        this.month = month
        this.day = day
    }
}
