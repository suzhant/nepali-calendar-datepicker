package com.example.android_nepali_calendar_picker_lib.listener

interface YearListener {

   fun onYearSelected(year: Int)

   fun getEndYear() : Int

   fun getStartYear() : Int
}