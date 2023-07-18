package com.example.android_nepali_calendar_picker_lib.utils

import android.content.Context
import androidx.annotation.IntRange
import com.example.android_nepali_calendar_picker_lib.model.Model
import com.example.android_nepali_calendar_picker_lib.model.NepaliEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime
import org.joda.time.Days
import java.io.IOException
import java.io.InputStreamReader
import java.util.Calendar

object DateConverter {
    /*
    get day of the week
     */
    private fun getDayOfWeek(day: Int): String {
        when (day) {
            1 -> return "Sunday"
            2 -> return "Monday"
            3 -> return "Tuesday"
            4 -> return "Wednesday"
            5 -> return "Thursday"
            6 -> return "Friday"
            7 -> return "Saturday"
        }
        return ""
    }

    private fun getWeekDay(
        @IntRange(from = 1970, to = 2090) yy: Int,
        @IntRange(from = 1, to = 12) mm: Int,
        @IntRange(from = 1, to = 32) dd: Int
    ): Int {
        val startDayOfMonth = NepaliDateData.startWeekDayMonthMap[yy][mm]
        val calDay = (startDayOfMonth + dd - 1) % 7
        return if(calDay== 0){
            7
        }else{
            calDay
        }
    }

    internal fun isSaturday(
        @IntRange(from = 1970, to = 2090) yy: Int,
        @IntRange(from = 1, to = 12) mm: Int,
        @IntRange(from = 1, to = 32) dd: Int
    ) : Boolean{
        val startDayOfMonth = NepaliDateData.startWeekDayMonthMap[yy][mm]
        val calDay = (startDayOfMonth + dd - 1) % 7
        return calDay == 0
    }

    private fun getEnglishDate(
        @IntRange(from = 1970, to = 2090) nepYY: Int,
        @IntRange(from = 1, to = 12) nepMM: Int,
        @IntRange(from = 1, to = 32) nepDD: Int
    ): Model {
        return if (isNepDateInConversionRange(
                nepYY,
                nepMM,
                nepDD
            )
        ) {
            val startingEngYear = 1913
            val startingEngMonth = 4
            val startingEngDay = 13
            val startingDayOfWeek =
                Calendar.SUNDAY // 1970/1/1 is Sunday /// based on www.ashesh.com.np/neplai-date-converter
            val startingNepYear = 1970
            val startingNepMonth = 1
            val startingNepDay = 1
            val tempModel = Model()
            var engMM: Int
            var engDD: Int
            var totalNepDaysCount: Long = 0

            //*count total no of days in nepali year from our starting range*//
            for (i in startingNepYear until nepYY) {
                for (j in 1..12) {
                    totalNepDaysCount += NepaliDateData.daysInMonthMap.get(i)[j]
                }
            }
            /*count total days in terms of month*/for (j in startingNepMonth until nepMM) {
                totalNepDaysCount += NepaliDateData.daysInMonthMap.get(nepYY)[j]
            }
            /*count total days in terms of date*/totalNepDaysCount += (nepDD - startingNepDay).toLong()
            val daysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            val daysInMonthOfLeapYear =
                intArrayOf(0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            var engYY: Int = startingEngYear
            engMM = startingEngMonth
            engDD = startingEngDay
            var endDayOfMonth: Int
            var dayOfWeek = startingDayOfWeek
            while (totalNepDaysCount != 0L) {
                endDayOfMonth = if (isEngLeapYear(engYY)) {
                    daysInMonthOfLeapYear[engMM]
                } else {
                    daysInMonth[engMM]
                }
                engDD++
                dayOfWeek++
                if (engDD > endDayOfMonth) {
                    engMM++
                    engDD = 1
                    if (engMM > 12) {
                        engYY++
                        engMM = 1
                    }
                }
                if (dayOfWeek > 7) {
                    dayOfWeek = 1
                }
                totalNepDaysCount--
            }
            tempModel.day = engDD
            tempModel.year =engYY
            tempModel.month = (engMM - 1)
            tempModel.dayOfWeek = dayOfWeek
            tempModel
        } else throw java.lang.IllegalArgumentException("Out of Range: Date is out of range to Convert")
    }

    private fun isEngLeapYear(year: Int): Boolean {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = year
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365
    }

    private fun isNepDateInConversionRange(yy: Int, mm: Int, dd: Int): Boolean {
        return yy in 1970..2090 && (mm in 1..12) && (dd in 1..32)
    }

    private fun isEngDateInConversionRange(yy: Int, mm: Int, dd: Int): Boolean {
        return (yy in 1913..2033) && (mm in 1..12) && (dd in 1..31)
    }

    private fun getNepaliDate(
        @IntRange(from = 1913 - 2033) engYY: Int,
        @IntRange(from = 1, to = 12) engMM: Int,
        @IntRange(from = 1, to = 31) engDD: Int
    ): Model {
        return if (isEngDateInConversionRange(engYY, engMM, engDD)) {
            val startingEngYear = 1913
            val startingEngMonth = 4
            val startingEngDay = 13
            val startingDayOfWeek = Calendar.SUNDAY // 1913/4/13 is a Sunday
            val startingNepYear = 1970
            val startingNepMonth = 1
            val startingNepDay = 1
            var nepMM: Int
            var nepDD: Int
            var dayOfWeek = startingDayOfWeek
            val tempModel = Model()

            /*
             Calendar currentEngDate = new GregorianCalendar();
             currentEngDate.set(engYY, engMM, engDD);
             Calendar baseEngDate = new GregorianCalendar();
             baseEngDate.set(startingEngYear, startingEngMonth, startingEngDay);
             long totalEngDaysCount = daysBetween(baseEngDate, currentEngDate);
             */

            /*calculate the days between two english date*/
            val base =
                DateTime(startingEngYear, startingEngMonth, startingEngDay, 0, 0) // June 20th, 2010
            val newDate = DateTime(engYY, engMM, engDD, 0, 0) // July 24th
            var totalEngDaysCount: Int = Days.daysBetween(base, newDate).days
            var nepYY: Int = startingNepYear
            nepMM = startingNepMonth
            nepDD = startingNepDay
            while (totalEngDaysCount != 0) {
                val daysInMonth: Int = NepaliDateData.daysInMonthMap.get(nepYY)[nepMM]
                nepDD++
                if (nepDD > daysInMonth) {
                    nepMM++
                    nepDD = 1
                }
                if (nepMM > 12) {
                    nepYY++
                    nepMM = 1
                }
                dayOfWeek++
                if (dayOfWeek > 7) {
                    dayOfWeek = 1
                }
                totalEngDaysCount--
            }
            tempModel.year = nepYY
            tempModel.month = nepMM - 1
            tempModel.day = nepDD
            tempModel.dayOfWeek = dayOfWeek
            tempModel
        } else throw java.lang.IllegalArgumentException("Out of Range: Date is out of range to Convert")
    }

    internal fun getNepaliDate(date: Calendar): Model {
        return getNepaliDate(
            date[Calendar.YEAR],
            date[Calendar.MONTH] + 1, date[Calendar.DAY_OF_MONTH]
        )
    }


    fun getNepaliMonth(index: Int) : String {
        return when(index){
            1 ->  "बैशाख"
            2 ->  "जेठ"
            3 ->  "असार"
            4 ->  "साउन"
            5 ->  "भदौ"
            6 -> "असोज"
            7 -> "कार्तिक"
            8 -> "मंसिर"
            9 -> "पुष"
            10 -> "माघ"
            11 -> "फाल्गुन"
            12 -> "चैत"
            else -> throw Exception("Invalid month")
        }
    }

    fun getEngDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getEngFirstDayOfMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)

        calendar.set(Calendar.DAY_OF_MONTH, 1)

        return calendar.get(Calendar.DAY_OF_WEEK)
    }

     fun convertToTimestamp(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar month starts from 0 (January is 0)
        calendar.set(Calendar.DAY_OF_MONTH, day)
         calendar.set(Calendar.HOUR_OF_DAY, 0)
         calendar.set(Calendar.MINUTE, 0)
         calendar.set(Calendar.SECOND, 0)
         calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getYearFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.YEAR)
    }

    fun getMonthFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.MONTH)
    }

    fun getDayFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun adToBs(engYear: Int,engMonth: Int,engDay: Int) : Model{
        return getNepaliDate(engYear,engMonth,engDay)
    }

    fun bsToAd(npYear: Int,npMonth: Int,npDay: Int) : Model{
        return getEnglishDate(npYear,npMonth,npDay)
    }

    fun getNepFirstDayOfMonth(year: Int, month: Int): Int {
        return NepaliDateData.startWeekDayMonthMap[year][month]
    }

    fun getNepDaysInMonth(year: Int, month: Int): Int {
        return NepaliDateData.daysInMonthMap[year][month]
    }

    internal fun initials(month: Int) : String {
        return when (month) {
            1 -> "a"
            2 -> "b"
            3 -> "c"
            4 -> "d"
            5 -> "e"
            6 -> "f"
            7 -> "g"
            8 -> "h"
            9 -> "i"
            10 -> "j"
            11 -> "k"
            12 -> "l"
            else -> ""
        }
    }

    internal fun getHeaderText(
        year: Int,
        month: Int,
        day: Int
    ): String {
        return TranslationService.translateDays(
            getDayOfWeek(
                day = getWeekDay(
                    yy = year,
                    mm = month,
                    dd = day
                )
            ).substring(0, 3)
        ) + ", " + getNepaliMonth(
            month
        ) + " " + TranslationService.translate(day.toString())
    }

    internal fun readJsonFile(context: Context, fileName: String): String? {
        //note: event available from 2070 to 2080
        try {
            val inputStream = context.assets.open(fileName)
            val reader = InputStreamReader(inputStream)
            val jsonString = reader.readText()
            reader.close()
            inputStream.close()

            return jsonString
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    internal fun parseJson(jsonString: String): MutableList<NepaliEvent> {
        val gson = Gson()
        val eventType = object : TypeToken<MutableList<NepaliEvent>>() {}.type
        return gson.fromJson(jsonString, eventType)
    }

}