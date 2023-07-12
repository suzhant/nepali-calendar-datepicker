package com.example.android_nepali_calendar_picker_lib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android_nepali_calendar_picker_lib.databinding.ViewpagerLayoutBinding
import com.example.android_nepali_calendar_picker_lib.listener.DateListener
import com.example.android_nepali_calendar_picker_lib.listener.DatePickerController
import com.example.android_nepali_calendar_picker_lib.model.MyCalendar
import com.example.android_nepali_calendar_picker_lib.utils.GridSpacingItemDecoration


internal class ViewPagerAdapter(
    private val listener: DateListener,
    private val calendar: MyCalendar,
    private val controller: DatePickerController
) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>(){
    private lateinit var context:Context
    private var selectedDayPosition: Int = -1

    inner class PagerViewHolder( val binding: ViewpagerLayoutBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        context = parent.context
        val view = ViewpagerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 12
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val month = calendar.month[position]
        with(holder.binding){
            val gridLayout = GridLayoutManager(context,7)
            calenderRecyclerView.addItemDecoration(GridSpacingItemDecoration(8))
            calenderRecyclerView.layoutManager = gridLayout
            val adapter = MonthAdapter(daysOfMonth = month.listOfDays, onClick = { day, year,dateId ->
                calendar.selectedDateId = dateId
                listener.onDateSet(
                    year = year,
                    month = (position+1).toString(),
                    day = day
                )
                updateSelectedDayPosition(position)
            }, selectedId = calendar.selectedDateId, controller = controller)
            calenderRecyclerView.adapter = adapter
        }
    }

    private fun updateSelectedDayPosition(position: Int) {
        val previousSelectedPosition = selectedDayPosition
        selectedDayPosition = position
        notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(selectedDayPosition)
    }

    fun setInitialSelectedPos(position: Int){
        selectedDayPosition = position
    }
}

