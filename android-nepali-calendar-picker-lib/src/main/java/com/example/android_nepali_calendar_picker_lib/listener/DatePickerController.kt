package com.example.android_nepali_calendar_picker_lib.listener

interface DatePickerController {

    fun getThemeColor() : Int

    fun getCurrentDateTimeStamp() : Long

    fun getCurrentDateId() : String

    fun getEndDateMillis() : Long

    fun getStartDateMillis() : Long

    fun getStartDay() : Int

    fun getEndDay() : Int

    fun getStartMonth() : Int

    fun getEndMonth() : Int

    fun getVibration() : Boolean

}