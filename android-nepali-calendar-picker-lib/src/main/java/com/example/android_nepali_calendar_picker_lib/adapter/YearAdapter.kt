package com.example.android_nepali_calendar_picker_lib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android_nepali_calendar_picker_lib.R
import com.example.android_nepali_calendar_picker_lib.databinding.WidgetYearLayoutBinding
import com.example.android_nepali_calendar_picker_lib.listener.DatePickerController
import com.example.android_nepali_calendar_picker_lib.listener.YearListener
import com.example.android_nepali_calendar_picker_lib.model.Year
import com.example.android_nepali_calendar_picker_lib.utils.TranslationService.translate

internal class YearAdapter(
    private val listOfYears: List<Year>,
    private val curYear : Int,
    private val listener: YearListener,
    private val controller: DatePickerController
) : RecyclerView.Adapter<YearAdapter.YearViewHolder>()  {

    private lateinit var context: Context
    inner class YearViewHolder(val binding: WidgetYearLayoutBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearViewHolder {
        context = parent.context
       val view = WidgetYearLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return YearViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfYears.size
    }

    override fun onBindViewHolder(holder: YearViewHolder, position: Int) {
        val year = listOfYears[position]
        with(holder.binding){
            txtYear.text = translate(year.name.toString())
            if (curYear == year.name){
                val drawable = ContextCompat.getDrawable(context, R.drawable.round_shape)
                if (controller.getThemeColor()!=-1){
                    val wrappedDrawable = DrawableCompat.wrap(drawable!!).mutate()
                    DrawableCompat.setTint(wrappedDrawable, controller.getThemeColor())
                }
                txtYear.background = drawable
                txtYear.setTextColor(ContextCompat.getColor(context, R.color.white))
            }else{
                txtYear.background = null
                txtYear.setTextColor(ContextCompat.getColor(context, R.color.material_on_surface_variant))
            }

            lytYear.setOnClickListener {
                if (year.isEnabled){
                    listener.onYearSelected(year.name)
                }
            }
        }
    }
}