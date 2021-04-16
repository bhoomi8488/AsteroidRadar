package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.udacity.asteroidradar.PictureOfDay

/**
 * Created by Bhoomi on 4/15/2021.
 */@Entity
data class ImageOfDay constructor( val mediaType: String,
                                  val title: String,
                                  @PrimaryKey
                        val url: String)

fun List<ImageOfDay>.asDomainModel(): List<PictureOfDay> {
    return map {
        PictureOfDay(
                url = it.url,
                title = it.title,
                mediaType = it.mediaType)
    }
}
