package com.example.calenderapp

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.android_nepali_calendar_picker_lib.NepaliDatePicker
import com.example.android_nepali_calendar_picker_lib.listener.DateListener
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter
import com.example.calenderapp.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class MainActivity : AppCompatActivity(), DateListener {

    private lateinit var binding: ActivityMainBinding
    private var npYear = 0
    private var npMonth = 0
    private var npDay = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalendar.setOnClickListener {
            val newFragment = NepaliDatePicker.newInstance(this)
            newFragment.show(supportFragmentManager, "Datepickerdialog")
            val maxDate = NepaliDatePicker.todayDateInMillis()
            newFragment.setEnd(maxDate)
            newFragment.setVibration(true)
//            newFragment.setMinAge(18)
//            newFragment.setThemeColor("#FF3700B3")
//            newFragment.setStart(2070,3,3)
//            val startDate = DateConverter.convertToTimestamp(2060,3,12)
//            val endDate = DateConverter.convertToTimestamp(2070,6,12)
//            val startDate = Model(2060,3,12)
//            val endDate = Model(2080,7,12)
//            newFragment.setRange(startDate,endDate)
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
          //  datePicker.show(supportFragmentManager,"MaterialDatePicker")
        }

        binding.btnAdToBs.setOnClickListener {
            val engYear = binding.etYy.text.toString()
            val engMonth = binding.etMm.text.toString()
            val engDay = binding.etDd.text.toString()
            if (engYear.isNotEmpty() && engDay.isNotEmpty() && engMonth.isNotEmpty()){
                val nepaliDate = DateConverter.adToBs(engYear.toInt(), engMonth.toInt(),engDay.toInt())
                val year = nepaliDate.year
                val month = nepaliDate.month + 1
                val day = nepaliDate.day
                val dateString = "$year/$month/$day"
                binding.txtConversionDate.text = dateString
            }
        }

        binding.btnBsToAd.setOnClickListener {
            val npYear = binding.etYy.text.toString()
            val npMonth = binding.etMm.text.toString()
            val npDay = binding.etDd.text.toString()
            if (npYear.isNotEmpty() && npMonth.isNotEmpty() && npDay.isNotEmpty()){
                val englishDate = DateConverter.bsToAd(npYear.toInt(), npMonth.toInt(),npDay.toInt())
                val year = englishDate.year
                val month = englishDate.month + 1
                val day = englishDate.day
                val dateString = "$year/$month/$day"
                binding.txtConversionDate.text = dateString
            }
        }

        binding.btnDayOfMonth.setOnClickListener {
            val npYear = binding.etYy.text.toString()
            val npMonth = binding.etMm.text.toString()
            if (npYear.isNotEmpty() && npMonth.isNotEmpty()){
                val days = DateConverter.getEngDaysInMonth(npYear.toInt(),npMonth.toInt())
                binding.txtConversionDate.text = days.toString()
            }
        }

        binding.btnFirstDayOfMonth.setOnClickListener {
            val npYear = binding.etYy.text.toString()
            val npMonth = binding.etMm.text.toString()
            if (npYear.isNotEmpty() && npMonth.isNotEmpty()){
                val days = DateConverter.getEngFirstDayOfMonth(npYear.toInt(),npMonth.toInt())
                binding.txtConversionDate.text = days.toString()
            }
        }

        binding.txtDate.setOnClickListener {
            val newFragment = NepaliDatePicker.newInstance(this)
            newFragment.show(supportFragmentManager, "Datepickerdialog")
            val maxDate = newFragment.getCurrentDateTimeStamp()
            newFragment.setEnd(maxDate)
            newFragment.setInitialDateSelection(npYear,npMonth,npDay)
        }

    }

    override fun onDateSet(year: String, month: String, day: String) {
        binding.txtDate.text = "$year-$month-$day"
        npYear = year.toInt()
        npMonth = month.toInt()
        npDay = day.toInt()
    }

}