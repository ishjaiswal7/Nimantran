package com.example.nimantran

import android.text.format.DateUtils
import android.widget.RadioButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import java.util.*


@BindingAdapter("image")
fun loadImage(view: android.widget.ImageView, url: String?) {
    if (url == null) {
    view.load(R.drawable.ic_person)
    } else {
        view.load(url)
    }
}

@BindingAdapter("humanize_date_text")
fun humanizeDate(view: TextView, date: Date?) {
    val now = System.currentTimeMillis()
    if (date != null) {
        when (val out =
            DateUtils.getRelativeTimeSpanString(date.time, now, DateUtils.DAY_IN_MILLIS)) {
            "0" -> view.text = "Today"
            else -> view.text = out
        }
    }
}

@BindingAdapter("humanize_time_text")
fun humanizeTime(view: TextView, date: Date?) {
    try {
        view.text = date?.let {
            DateUtils.formatDateTime(
                view.context,
                it.time,
                DateUtils.FORMAT_SHOW_TIME
            )
        }
    } catch (e: Exception) {
        view.text = "00:00"
    }
}

@BindingAdapter("price_text")
fun priceInRupeesText(view: TextView, price: Double) {
    try {
        view.text = "₹ $price"
    } catch (e: Exception) {
        view.text = "₹ 0.0"
    }
}

@BindingAdapter("set_female_checked")
fun setCheckedFemale(rb: RadioButton, gender: String?) {
    rb.isChecked = gender.equals("Female")
}

@BindingAdapter("set_male_checked")
fun setCheckedMale(rb: RadioButton, gender: String?) {
    rb.isChecked =  gender.equals("Male")
}