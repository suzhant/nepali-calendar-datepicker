package com.example.android_nepali_calendar_picker_lib.listener

import com.example.android_nepali_calendar_picker_lib.model.Model

interface DatePickerFeatures {

    fun setThemeColor(hex : String)

    fun setVibration(vibrate : Boolean)

    fun setRange(startDate: Long, endDate: Long)

    fun setRange(startDate: Model, endDate: Model)

    fun setStart(year: Int,month: Int,day: Int)

    fun setEnd(year: Int,month: Int,day: Int)

    fun setEnd(timeInMillis: Long)

    fun setStart(timeInMillis: Long)

    fun setMinAge(age: Int)

    fun setInitialDateSelection(year: Int, month: Int, day: Int)
}