package com.mario.citas.core.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mario.citas.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val datePickerOnDataSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat(format, Locale.UK)
            setText(sdf.format(myCalendar.time))
        }

    setOnClickListener {
        DatePickerDialog(
            context,
            R.style.PickerTheme,
            datePickerOnDataSetListener,
            myCalendar
                .get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            show()
        }
    }
}

fun EditText.initPiker() {
    this.isFocusableInTouchMode = false
    this.isClickable = true
    this.isFocusable = false
}

fun EditText.showTimePiker(fragmentManager: FragmentManager) {
    val timePicker = TimePickerDialog { this.setText(it) }
    timePicker.show(fragmentManager, "timePicker")
}

class HourPickerDialog(
    context: Context,
    timeSetListener: OnTimeSetListener,
    initialHourOfDay: Int,
    initialMinute: Int = 0,
    is24HourView: Boolean = true,
) : TimePickerDialog(context, timeSetListener, initialHourOfDay, initialMinute, is24HourView) {

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        onClick(this, BUTTON_POSITIVE)
        dismiss()
    }
}

class TimePickerDialog(val listener: (String) -> Unit) :
    DialogFragment(),
    TimePickerDialog.OnTimeSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute =0
        val picker = TimePickerDialog(
            activity as Context,
            R.style.PickerTheme, this, hour, minute, true
        )
        return picker
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if (minute < 10) {
            listener("$hourOfDay:0$minute")
        } else {
            listener("$hourOfDay:$minute")
        }
    }
}