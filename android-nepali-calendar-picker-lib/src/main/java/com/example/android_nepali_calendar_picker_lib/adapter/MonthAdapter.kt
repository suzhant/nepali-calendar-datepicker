package com.example.android_nepali_calendar_picker_lib.adapter

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android_nepali_calendar_picker_lib.R
import com.example.android_nepali_calendar_picker_lib.databinding.CalenderCellBinding
import com.example.android_nepali_calendar_picker_lib.listener.DatePickerController
import com.example.android_nepali_calendar_picker_lib.model.MyDate
import com.example.android_nepali_calendar_picker_lib.utils.DateConverter
import com.example.android_nepali_calendar_picker_lib.utils.TranslationService.translate
import com.example.android_nepali_calendar_picker_lib.utils.TranslationService.translateDays

internal class MonthAdapter(
    private val daysOfMonth: List<MyDate>,
    private val onClick : (day : String,year:String,selectedId: String) -> Unit,
    private var selectedId: String,
    private var controller: DatePickerController
) : RecyclerView.Adapter<MonthAdapter.CalenderViewHolder>(){

    private lateinit var context: Context
    inner class CalenderViewHolder( val binding: CalenderCellBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalenderViewHolder {
        context = parent.context
        val view = CalenderCellBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CalenderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalenderViewHolder, position: Int) {
        val dateObj = daysOfMonth[position]
        with(holder.binding){
            calenderDay.text = if (dateObj.day.isDigitsOnly()){
                translate(dateObj.day)
            }else{
                translateDays(dateObj.day)
            }

            if (dateObj.day.isDigitsOnly()){
                if (dateObj.id !=null){
                    if (DateConverter.isSaturday(
                            yy = dateObj.year.toInt(),
                            mm = dateObj.month.toInt(),
                            dd = dateObj.day.toInt()
                        )){
                        calenderDay.setTextColor(ContextCompat.getColor(context, R.color.imperial_red))
                    }
                    if (dateObj.id == selectedId){
                        val drawable = ContextCompat.getDrawable(context, R.drawable.round_shape)
                        val convertedDrawable = adjustDrawableColor(drawable!!)
                        calenderDay.background = convertedDrawable
                        calenderDay.setTextColor(ContextCompat.getColor(context, R.color.white))
                    }
                }
            }
            if (dateObj.id == null){
                calenderDay.setTypeface(null,Typeface.BOLD)
                calenderDay.setTextColor(ContextCompat.getColor(context, R.color.dim_gray))
            }

            if (controller.getCurrentDateId() == dateObj.id){
                if (selectedId == dateObj.id){
                    calenderDay.setTextColor(ContextCompat.getColor(context, R.color.white))
                }else{
                    val drawable = ContextCompat.getDrawable(context, R.drawable.hollow_round_shape)
                    val convertedDrawable = adjustDrawableColor(drawable!!)
                    calenderDay.background = convertedDrawable

                    var color = ContextCompat.getColor(context, R.color.teal_700)
                    if (controller.getThemeColor()!=-1){
                        color = controller.getThemeColor()
                    }
                    calenderDay.setTextColor(color)
                }
                calenderDay.setTypeface(null,Typeface.BOLD)
            }

            if (!dateObj.isEnabled){
                calenderDay.setTextColor(ContextCompat.getColor(context, com.google.android.material.R.color.material_on_surface_disabled))
            }


            holder.itemView.setOnClickListener {
                if (dateObj.day.isDigitsOnly() && dateObj.isEnabled){
                    selectedId = dateObj.id.toString()
                    if (controller.getVibration()){
                        vibrate(view = it)
                    }
                    onClick(dateObj.day,dateObj.year, selectedId)
                }
            }
        }
    }

    private fun vibrate(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
    }

    private fun adjustDrawableColor(drawable:Drawable) : Drawable{
        if (controller.getThemeColor()!=-1){
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, controller.getThemeColor())
            return wrappedDrawable
        }
        return drawable
    }

}