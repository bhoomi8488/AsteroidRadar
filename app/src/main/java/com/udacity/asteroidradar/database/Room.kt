package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.PictureOfDay

/**
 * Created by Bhoomi on 4/15/2021.
 */

@Dao
interface PictureOfDayDao {
    @Query("select * from ImageOfDay")
    fun getImage(): LiveData<List<ImageOfDay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: ImageOfDay)

    @Query("delete from ImageOfDay")
    fun clear()

}

@Database(entities = [ImageOfDay::class], version = 1)
abstract class ImageOfDayDatabase : RoomDatabase() {
    abstract val pictureDao: PictureOfDayDao
}

private lateinit var INSTANCE: ImageOfDayDatabase

fun getDatabase(context: Context): ImageOfDayDatabase {
    synchronized(ImageOfDayDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    ImageOfDayDatabase::class.java,
                    "images").build()
        }
    }
    return INSTANCE
}