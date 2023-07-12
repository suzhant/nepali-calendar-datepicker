package com.example.android_nepali_calendar_picker_lib.utils

import android.util.Log
import com.example.android_nepali_calendar_picker_lib.extensions.countSlashOccurrences
import com.example.android_nepali_calendar_picker_lib.extensions.insertSlashAtPositions
import com.example.android_nepali_calendar_picker_lib.extensions.removeSlash

internal object DateValidation {

    internal fun validateDateMask(input: String): String {
        val sb = StringBuilder(input)
        sb.insertSlashAtPositions()

        val count = sb.countSlashOccurrences()
        Log.d("slash",count.toString())
        if (count > 2){
            sb.clear()
            val newString = input.removeSlash()
            sb.append(newString)
            sb.insertSlashAtPositions()
        }

        return sb.toString()
    }

    internal fun validateDate(year: Int,month: Int,day: Int): Boolean{
        if (year < 1970 || year > 2090 || month > 12 || day > 32){
            return false
        }
        val validDay = NepaliDateData.daysInMonthMap[year][month]
        if (day > validDay){
            return false
        }
        return true
    }

}