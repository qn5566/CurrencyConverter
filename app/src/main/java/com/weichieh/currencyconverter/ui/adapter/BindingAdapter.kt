package com.weichieh.currencyconverter.ui.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Custom BindingAdapter used to format double values and set the formatted value to a TextView.
 * The formatted value is rounded to 6 decimal places.
 * The BindingAdapter method is named "getFormatValue".
 * The BindingAdapter attribute is named "getFormatValue".
 *
 * @param textView the TextView to which the formatted value should be set
 * @param value the value to be formatted
 */

@BindingAdapter("getFormatValue")
fun getFormatValue(textView: TextView, value: Double) {
    val df = DecimalFormat("#.######")
    df.roundingMode = RoundingMode.CEILING

    var value = df.format(value).toDouble()
    var text: String = value.toString()
    textView.text = text
}