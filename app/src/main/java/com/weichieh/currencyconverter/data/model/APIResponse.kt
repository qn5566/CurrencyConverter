package com.weichieh.currencyconverter.data.model

import com.google.gson.annotations.SerializedName

data class APIResponse(
    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("base")
    val base: String,

    @SerializedName("rates")
    val rates: Rate?
)