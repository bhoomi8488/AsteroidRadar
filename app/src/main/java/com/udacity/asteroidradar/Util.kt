package com.udacity.asteroidradar

import android.annotation.SuppressLint
import android.content.res.Resources
import java.text.SimpleDateFormat

/**
 * Created by Bhoomi on 4/15/2021.
 */
@SuppressLint("SimpleDateFormat")
fun convertStringToDateString(systemTime: String, resources: Resources): String {
    return SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
            .format(systemTime).toString()
}