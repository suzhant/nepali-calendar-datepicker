package com.example.android_nepali_calendar_picker_lib

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.util.keyIterator
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.android_nepali_calendar_picker_lib.adapter.ViewPagerAdapter
import com.example.android_nepali_calendar_picker_lib.adapter.YearAdapter
import com.example.android_nepali_calendar_picker_lib.databinding.FragmentDatePickerDialogBinding
import com.example.android_nepali_calendar_picker_lib.extensions.removeLeadingZero
import com.example.android_nepali_calendar_picker_lib.listener.DateListener
import com.example.android_nepali_calendar_picker_lib.listener.DatePickerController
import com.example.android_nepali_calendar_picker_lib.listener.DatePickerFeatures
import com.example.android_nepali_calendar_picker_lib.listener.YearListener
import com.example.android_nepali_calendar_picker_lib.model.Model
import com.example.android_nepali_calendar_picker_lib.model.Month
import com.example.android_nepali_calendar_picker_lib.model.MyCalendar
import com.example.android_nepali_calendar_picker_lib.model.MyDate
import com.example.android_nepali_calendar_picker_lib.model.NepaliEvent
import com.example.android_nepali_calendar_picker_lib.model.Year
import com.example.android_nepali_calendar_picker_lib.utils.Constants
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter.convertToTimestamp
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter.getDayFromTimestamp
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter.getHeaderText
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter.getMonthFromTimestamp
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter.getNepaliDate
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter.getYearFromTimestamp
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter.initials
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter.parseJson
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter.readJsonFile
import com.example.android_nepali_calendar_picker_lib.utils.DateValidation.validateDate
import com.example.android_nepali_calendar_picker_lib.utils.DateValidation.validateDateMask
import com.example.android_nepali_calendar_picker_lib.utils.KeyBoardUtils.hideKeyboard
import com.example.android_nepali_calendar_picker_lib.utils.KeyBoardUtils.isSoftKeyboardVisible
import com.example.android_nepali_calendar_picker_lib.utils.KeyBoardUtils.showKeyboard
import com.example.android_nepali_calendar_picker_lib.utils.NepaliDateData
import com.example.android_nepali_calendar_picker_lib.utils.TranslationService.translate
import com.example.android_nepali_calendar_picker_lib.utils.TranslationService.translateToEng
import java.util.Calendar


/**
 *  Created by sushant shrestha on 2023/06/28
 **/

class NepaliDatePicker : DialogFragment(), DateListener, YearListener, DatePickerController,DatePickerFeatures{

    private var _binding : FragmentDatePickerDialogBinding ?= null
    private val binding get() = _binding!!
    private var calendar: Calendar = Calendar.getInstance()
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var myCal: MyCalendar
    private var currentDateMillis: Long = 0L
    private var selectedDateID: String = ""
    private var currentDateId: String = ""
    private var mCalendar = Model()
    private lateinit var yearAdapter: YearAdapter
    private lateinit var listener : DateListener
    private var mThemeColor: Int = -1
    private var endTimeInMillis : Long = -1L
    private var startTimeInMillis : Long = -1L
    private var currentMonth = 0
    private var currentYear = 0
    private var currentDay = 0
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0
    private var vibration : Boolean = false
    private var isInputMode = false
    private var inputMonth = 0
    private var inputDay = 0
    private var inputYear = 0
    private lateinit var textWatcher : TextWatcher
    private var eventList = mutableListOf<NepaliEvent>()

    companion object {
        @JvmStatic
        fun newInstance(listener: DateListener) : NepaliDatePicker {
            val dialog = NepaliDatePicker().apply {
                this.listener = listener
                currentDateMillis = getCurrentNepaliDateInMillis()
                currentDateId = initials(mCalendar.month + 1) + currentDateMillis
                selectedDateID = initials(mCalendar.month +1 ) + currentDateMillis
                Log.d("currentDate",currentDateMillis.toString())
                if (endTimeInMillis == -1L) {
                    endTimeInMillis = convertToTimestamp(NepaliDateData.getMaxYear(), 12, 32)
                }
                if (startTimeInMillis == -1L) {
                    startTimeInMillis = convertToTimestamp(NepaliDateData.getMinYear(), 1, 1)
                }
                currentMonth = mCalendar.month
                currentYear = mCalendar.year
                currentDay = mCalendar.day
                selectedYear = currentYear
                selectedMonth = currentMonth
                selectedDay = currentDay
            }
            return dialog
        }

        @JvmStatic
        fun todayDateInMillis() : Long{
            NepaliDatePicker().apply {
               return getCurrentNepaliDateInMillis()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            mThemeColor = savedInstanceState.getInt(Constants.KEY_THEME_COLOR)
            currentMonth = savedInstanceState.getInt(Constants.MONTH_POS)
            currentDay = savedInstanceState.getInt(Constants.CURRENT_DAY)
            currentDateMillis = savedInstanceState.getLong(Constants.CURRENT_DATE_MILLIS)
            startTimeInMillis = savedInstanceState.getLong(Constants.START_DATE_MILLIS)
            endTimeInMillis = savedInstanceState.getLong(Constants.END_DATE_MILLIS)
            currentYear = savedInstanceState.getInt(Constants.CURRENT_YEAR)
            selectedDateID = savedInstanceState.getString(Constants.SELECTED_DATE_ID)!!
            vibration = savedInstanceState.getBoolean(Constants.VIBRATION)
            selectedMonth = savedInstanceState.getInt(Constants.SELECTED_MONTH)
            selectedYear = savedInstanceState.getInt(Constants.SELECTED_YEAR)
            selectedDay = savedInstanceState.getInt(Constants.SELECTED_DAY)
            listener = context as DateListener
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.KEY_THEME_COLOR,mThemeColor)
        outState.putInt(Constants.MONTH_POS,currentMonth)
        outState.putLong(Constants.CURRENT_DATE_MILLIS,currentDateMillis)
        outState.putLong(Constants.START_DATE_MILLIS,startTimeInMillis)
        outState.putLong(Constants.END_DATE_MILLIS,endTimeInMillis)
        outState.putInt(Constants.CURRENT_YEAR,currentYear)
        outState.putString(Constants.SELECTED_DATE_ID,selectedDateID)
        outState.putInt(Constants.CURRENT_DAY,currentDay)
        outState.putBoolean(Constants.VIBRATION,vibration)
        outState.putInt(Constants.SELECTED_YEAR,selectedYear)
        outState.putInt(Constants.SELECTED_MONTH,selectedMonth)
        outState.putInt(Constants.SELECTED_DAY,selectedDay)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDatePickerDialogBinding.inflate(inflater, container, false)

        if (mThemeColor!=-1){
            setViewColor()
        }

        initialize(initialSelection = getNepaliDate(calendar))
        binding.mdtpDatePickerYear.text = translate(currentYear.toString())

        binding.mdtpDatePickerDay.text = getHeaderText(
            year = selectedYear,
            month = selectedMonth+1,
            day = selectedDay
        )
        val nepaliMonth = DateConverter.getNepaliMonth(currentMonth + 1)
        binding.headerButton.text = "$nepaliMonth $currentDay, $currentYear"

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                changeButtonState(position)
                currentMonth = position
                val currentMonth = DateConverter.getNepaliMonth(position+1)
                val currentYear = translate(currentYear.toString())
                binding.headerButton.text = "$currentMonth $currentYear"
            }
        })

        binding.buttonFront.setOnClickListener {
            binding.viewpager.currentItem = binding.viewpager.currentItem + 1
        }

        binding.buttonBack.setOnClickListener {
            binding.viewpager.currentItem = binding.viewpager.currentItem - 1
        }

        binding.mdtpCancel.setOnClickListener {
            dismiss()
        }
        binding.mdtpOk.setOnClickListener {
            if (isInputMode){
                listener.onDateSet(
                    year = inputYear.toString() ,
                    month = (inputMonth).toString() ,
                    day = inputDay.toString()
                )
            }else{
                listener.onDateSet(
                    year = selectedYear.toString() ,
                    month = (selectedMonth + 1).toString() ,
                    day = selectedDay.toString()
                )
            }


            dismiss()
        }

        binding.toggleButtonGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                binding.mdtpDatePickerYear.id -> {
                    binding.recyclerYear.visibility = View.VISIBLE
                    binding.viewpager.visibility = View.GONE
                    initYearRecycler()
                }

                binding.mdtpDatePickerDay.id -> {
                    binding.recyclerYear.visibility = View.GONE
                    binding.viewpager.visibility = View.VISIBLE
                    initRecycler()
                }
            }

        }

        binding.headerButton.setOnClickListener {
            if (binding.mdtpDatePickerDay.isChecked){
                binding.mdtpDatePickerYear.isChecked =  true
            }else{
                binding.mdtpDatePickerDay.isChecked = true
            }

        }

        binding.btnDateInput.setOnClickListener {
            isInputMode = true
            binding.mdtpOk.isEnabled = false
            //hide datePicker view
            binding.toggleButtonGroup.visibility = View.GONE
            binding.recyclerYear.visibility = View.GONE
            binding.viewpager.visibility = View.GONE
            binding.linearLayout.visibility = View.GONE
            binding.btnDateInput.visibility = View.GONE
            //show date input view
            binding.layoutHeaderText.root.visibility = View.VISIBLE
            binding.layoutHeaderText.date.text = binding.mdtpDatePickerDay.text
            binding.modalDate.root.visibility = View.VISIBLE
            binding.btnCalendarPicker.visibility = View.VISIBLE
            val editText = binding.modalDate.editTextModal
            editText.requestFocus()
            // Show the soft keyboard
            showKeyboard(editText,requireContext())

             textWatcher = object : TextWatcher {
                 var isDeleting: Boolean = false
                 var ignore = false

                 override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //check if the character has been removed
                    isDeleting = before > count

                    if(ignore || isDeleting){
                        ignore = false
                        isDeleting = false
                        binding.mdtpOk.isEnabled = s?.length!! > 9
                        return
                    }

                    val inputText = s.toString()
                    //implement date mask
                    val modifiedText = validateDateMask(inputText)
                    if (modifiedText.length > 9){
                        inputMonth = translateToEng((modifiedText.substring(0, 2))).removeLeadingZero().toInt()
                        inputDay = translateToEng((modifiedText.substring(3, 5))).removeLeadingZero().toInt()
                        inputYear = translateToEng(modifiedText.substring(6, 10)).toInt()
                        //validate date
                        val isDobCorrect = validateDate(inputYear,inputMonth,inputDay)
                        if (!isDobCorrect) {
                            binding.modalDate.inputLayoutModal.error = getString(R.string.input_error_message)
                            binding.mdtpOk.isEnabled = false
                            return
                        }
                        binding.modalDate.inputLayoutModal.isErrorEnabled = false
                        binding.mdtpOk.isEnabled = true
                        val mText = getHeaderText(
                            year = inputYear,
                            month = inputMonth,
                            day = inputDay
                        )
                        binding.layoutHeaderText.date.text = mText
                    }

                    ignore = true
                    editText.setText(modifiedText)
                    editText.setSelection(modifiedText.length)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
            editText.addTextChangedListener(textWatcher)

            if (binding.modalDate.editTextModal.text!!.isNotEmpty() && binding.modalDate.editTextModal.text!!.length > 9){
                performInputValidation()
            }
        }

        binding.btnCalendarPicker.setOnClickListener {
            isInputMode = false
            binding.mdtpOk.isEnabled = true
            //hide date input view
            binding.btnCalendarPicker.visibility = View.GONE
            binding.modalDate.root.visibility = View.GONE
            binding.layoutHeaderText.root.visibility = View.GONE
            binding.toggleButtonGroup.clearCheck()
            //show calendar picker view
            binding.mdtpDatePickerDay.isChecked = true
            binding.btnDateInput.visibility = View.VISIBLE
            binding.toggleButtonGroup.visibility = View.VISIBLE
            binding.linearLayout.visibility = View.VISIBLE
            if (isSoftKeyboardVisible(requireContext())){
                hideKeyboard(requireContext(),requireView())
            }
            binding.modalDate.editTextModal.removeTextChangedListener(textWatcher)
        }

        initRecycler()
        observeViewPager()

        return binding.root
    }

    private fun performInputValidation(){
        val isDobCorrect = validateDate(inputYear,inputMonth,inputDay)
        if (!isDobCorrect) {
            binding.modalDate.inputLayoutModal.error = getString(R.string.input_error_message)
            binding.mdtpOk.isEnabled = false
            return
        }
        binding.modalDate.inputLayoutModal.isErrorEnabled = false
        binding.mdtpOk.isEnabled = true
    }
    private fun initYearRecycler() {
       yearAdapter = YearAdapter(getYearList(),currentYear,this,this)
        val gridLayoutManager = GridLayoutManager(context,3)
        binding.recyclerYear.adapter = yearAdapter
        binding.recyclerYear.layoutManager = gridLayoutManager
        binding.recyclerYear.scrollToPosition(calculateYearPos())
    }

    private fun observeViewPager(){
        binding.viewpager.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val width = binding.viewpager.width

                val layoutParam = binding.recyclerYear.layoutParams
                layoutParam.width = width
                binding.recyclerYear.layoutParams = layoutParam
                binding.recyclerYear.requestLayout()

                val modalParam = binding.modalDate.root.layoutParams
                modalParam?.width = width
                binding.modalDate.root.layoutParams = modalParam
                binding.modalDate.root.requestLayout()

                // Remove the listener to avoid multiple callbacks
                binding.viewpager.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

    }

    private fun changeButtonState(position : Int){
        binding.buttonFront.isEnabled = (position != 11)
        binding.buttonFront.imageTintList = ContextCompat.getColorStateList(requireContext(), if (position != 11) R.color.black else R.color.gray)
        binding.buttonBack.isEnabled = (position != 0)
        binding.buttonBack.imageTintList = ContextCompat.getColorStateList(requireContext(), if (position != 0) R.color.black else R.color.gray)
    }
    private fun calculateYearPos(): Int {
        val startYear = getStartYear()
        return currentYear - startYear - 5
    }

    private fun getYearList(): List<Year>{
        val listOfYear = mutableListOf<Year>()
        val year = NepaliDateData.daysInMonthMap.keyIterator()
        for(curYear in year){
            if (curYear < getEndYear()+1 && curYear > getStartYear()-1){
                val yearModel = Year(name = curYear, isEnabled = curYear < getEndYear()+1 && curYear > getStartYear()-1)
                listOfYear.add(yearModel)
            }
        }
        return listOfYear
    }

    private fun initRecycler() {
        viewPagerAdapter = ViewPagerAdapter(
            listener = this,
            calendar = myCal,
            controller = this
        )
        binding.viewpager.adapter = viewPagerAdapter
        binding.viewpager.setCurrentItem(currentMonth,false)
        viewPagerAdapter.setInitialSelectedPos(currentMonth)
    }

    private fun getCurrentNepaliDateInMillis(): Long{
        mCalendar = getNepaliDate(calendar)
        val npYY = mCalendar.year
        val npMM = mCalendar.month + 1
        val npDD = mCalendar.day
        Log.d("nepDate","${mCalendar.year}${mCalendar.month + 1}${mCalendar.day}")
        return convertToTimestamp(npYY,npMM ,npDD)
    }

    private fun getDaysWithMonth(year: Int): List<Month>{
        val months = mutableListOf<Month>()
        for (curMonth in 1..12) {
            val days = NepaliDateData.daysInMonthMap[year][curMonth]
            val firstDayOfMonth = NepaliDateData.startWeekDayMonthMap[year][curMonth]
            val listOfDays = fetchDays(
                year = year,
                noOfDays = days,
                firstDayOfMonth = firstDayOfMonth,
                month = curMonth
            )
            val month = Month(
                noOfDays = days,
                firstDayOfMonth = firstDayOfMonth,
                listOfDays = listOfDays,
                year = year
            )
            months.add(month)
        }
        return months
    }

    private fun initCalendar(): MyCalendar {
     //   fetchEvents(currentYear)
        val months = getDaysWithMonth(currentYear)
        return MyCalendar(
            month = months,
            currentDateId = currentDateId,
            selectedDateId = selectedDateID,
            year = currentYear,
            themeColor = if (mThemeColor!=-1) getThemeColor() else -1
        )
    }

    override fun onYearSelected(year: Int) {
        currentYear = year
        myCal = initCalendar()
        binding.mdtpDatePickerDay.isChecked = true
        binding.mdtpDatePickerYear.text = translate(currentYear.toString())
    }

    private fun setSelectedId(year: Int) {
        val time = convertToTimestamp(year, currentMonth + 1, currentDay)
        if (time !in startTimeInMillis..endTimeInMillis) {
            selectedDateID = when (currentYear) {
                getStartYear() -> {
                    val time1 = convertToTimestamp(year, getStartMonth() + 1, getStartDay())
                    currentMonth = getStartMonth()
                    currentDay = getStartDay()
                    initials(currentMonth + 1) + time1
                }
                getEndYear() -> {
                    val time2 = convertToTimestamp(year, getEndMonth() + 1, getEndDay())
                    currentMonth = getEndMonth()
                    currentDay = getEndDay()
                    initials(currentMonth + 1) + time2
                }
                else -> {
                    initials(currentMonth + 1) + time
                }
            }
        } else {
            selectedDateID = initials(currentMonth + 1) + time
        }
    }

    private fun updateDay(year: Int,month: Int,day: Int){
        val mText = getHeaderText(
            year = year,
            month = month,
            day = day
        )
        selectedYear = year
        selectedDay = day
        selectedMonth = month - 1
        binding.mdtpDatePickerDay.text = mText
    }

    override fun onDateSet(year: String, month: String, day: String) {
        selectedDateID = myCal.selectedDateId
        updateDay(year.toInt(),month.toInt(),day.toInt())
    }

     private fun initialize(initialSelection: Model?) {
        mCalendar.year = (initialSelection!!.year)
        mCalendar.month = (initialSelection.month)
        mCalendar.day = (initialSelection.day)
        myCal = initCalendar()
    }


    private fun getMonthList() : List<Month>{
        val years = NepaliDateData.daysInMonthMap.keyIterator()
        val firstYear= years.asSequence().first()
        val lastYear = years.asSequence().last()
        Log.d("years","$firstYear $lastYear")
        val months = mutableListOf<Month>()
        for (year in firstYear..lastYear){
            for (curMonth in 1..12) {
                val days = NepaliDateData.daysInMonthMap[year][curMonth]
                val firstDayOfMonth = NepaliDateData.startWeekDayMonthMap[year][curMonth]
                val listOfDays = fetchDays(
                    year = year,
                    noOfDays = days,
                    firstDayOfMonth = firstDayOfMonth,
                    month = curMonth
                )
                val month = Month(
                    noOfDays = days,
                    firstDayOfMonth = firstDayOfMonth,
                    listOfDays = listOfDays,
                    year = year
                )
                months.add(month)
            }
        }

        Log.d("months", months[120].toString())
        return months
    }

    private fun fetchDays(
        year: Int,
        noOfDays: Int,
        firstDayOfMonth: Int,
        month: Int
    ) : List<MyDate>{
        val listOfDays = mutableListOf<MyDate>()
        val shortDays = listOf("Sun","Mon","Tue","Wed","Thr","Fri","Sat")
        for (day in shortDays){
            val date = MyDate(
                id = null,
                year = year.toString(),
                month = month.toString(),
                day = day,
                event = null
            )
            listOfDays.add(date)
        }
        for (i in 1..42){
            if (i<firstDayOfMonth){
                val date = MyDate(null,year.toString(),month.toString()," ", event = null)
                listOfDays.add(date)
            }else{
                val day = i - (firstDayOfMonth-1)
                if (day<=noOfDays){
                    val time = convertToTimestamp(year, month, day)
                    val id = initials(month) + time
                    val date = MyDate(
                        id = id,
                        year = year.toString(),
                        month = month.toString(),
                        day = day.toString(),
                        isEnabled = time in startTimeInMillis..endTimeInMillis,
                        event = if (eventList.isNotEmpty()){
                            eventList[month - 1].days[day - 1]
                        } else null
                    )
                    listOfDays.add(date)
                }

            }
        }
        return listOfDays
    }

    private fun fetchEvents(currentYear: Int){
        eventList.clear()
        if (currentYear in 2000..2080){
            val jsonString = readJsonFile(requireContext(), "years/$currentYear.json")
            jsonString?.let {
                val nepaliEventsList = parseJson(it)
                // Use the nepaliEventsList as per your requirements
                eventList = nepaliEventsList
            }
        }
    }

     override fun setThemeColor(hex: String) {
         try {
             val color = Color.parseColor(hex)
             mThemeColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color))
         }catch (e:Exception){
             throw Exception(e.toString())
         }

    }

    override fun getThemeColor(): Int {
        return mThemeColor
    }

    private fun setViewColor(){
        binding.toggleButtonGroup.setBackgroundColor(mThemeColor)
        binding.mdtpOk.setTextColor(mThemeColor)
        binding.mdtpCancel.setTextColor(mThemeColor)
    }

     override fun setEnd(year: Int, month: Int, day: Int){
         endTimeInMillis = convertToTimestamp(year,month,day+1)
    }

    override fun setEnd(timeInMillis: Long){
        endTimeInMillis = timeInMillis
    }

    override fun setStart(year: Int, month: Int, day: Int){
        startTimeInMillis = convertToTimestamp(year,month,day)
    }

    override fun setStart(timeInMillis: Long) {
         startTimeInMillis = timeInMillis
    }

    override fun setRange(startDate: Long, endDate: Long){
        startTimeInMillis = startDate
        endTimeInMillis = endDate
    }

    override fun setRange(startDate: Model, endDate: Model){
        val startYY = startDate.year
        val startMM = startDate.month
        val startDD = startDate.day
        val endYY = endDate.year
        val endMM = endDate.month
        val endDD = endDate.day
        startTimeInMillis = convertToTimestamp(startYY,startMM,startDD)
        endTimeInMillis = convertToTimestamp(endYY,endMM,endDD)
    }

    override fun getStartDateMillis(): Long {
        return startTimeInMillis
    }

    override fun getEndDateMillis(): Long {
        return endTimeInMillis
    }

    override fun getCurrentDateTimeStamp(): Long {
        return currentDateMillis
    }


    override fun getCurrentDateId(): String {
        return currentDateId
    }

    override fun getEndYear(): Int {
        return getYearFromTimestamp(endTimeInMillis)
    }

    override fun getStartYear(): Int {
        return getYearFromTimestamp(startTimeInMillis)
    }

    override fun getStartMonth(): Int {
        return getMonthFromTimestamp(startTimeInMillis)
    }

    override fun getEndMonth(): Int {
        return getMonthFromTimestamp(endTimeInMillis)
    }

    override fun getStartDay(): Int {
        return getDayFromTimestamp(startTimeInMillis)
    }

    override fun getEndDay(): Int {
        return getDayFromTimestamp(endTimeInMillis)
    }

    override fun setMinAge(age: Int) {
        val endYear = currentYear - age
        endTimeInMillis = convertToTimestamp(endYear,currentMonth + 1, currentDay)
        currentYear = endYear
        selectedDateID = initials(currentMonth + 1) + endTimeInMillis
    }

    override fun setVibration(vibrate: Boolean) {
        vibration = vibrate
    }

    override fun getVibration(): Boolean {
        return vibration
    }

    override fun setInitialDateSelection(year: Int, month: Int, day: Int) {
        val timeInMillis = convertToTimestamp(year, month, day)
        selectedDateID = initials(month) + timeInMillis
        currentMonth = month - 1
        currentYear = year
        currentDay = day
        selectedYear = year
        selectedMonth = month - 1
        selectedDay = day
    }

}