/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.api.getStartDate
import com.udacity.asteroidradar.api.getYesterDayDate
import com.udacity.asteroidradar.model.AsteroidModel

@Dao
interface AsteroidDao {

    //Insert the list of Asteroid data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: AsteroidModel)

    //Get the list of Asteroid data
    @Query("select * from database_asteroid ORDER BY date(closeApproachDate) DESC ")
    fun getAsteroid(): LiveData<List<AsteroidModel>>

    //Get the currrent date record of Asteroid
    @Query("SELECT * FROM database_asteroid WHERE closeApproachDate = DATE('now', 'localtime') ORDER BY date(closeApproachDate) DESC")
    fun getTodaysAsteroid():LiveData<List<AsteroidModel>>

    //Get the Week Asteroid data starting from today to next 7 days
    @Query("SELECT * FROM database_asteroid WHERE DATE(closeApproachDate) >= DATE('now', 'weekday 1', '-6 days') ORDER BY date(closeApproachDate) DESC")
    fun getWeekAsteroid():LiveData<List<AsteroidModel>>

    //delete the data for previous days
    @Query( "DELETE FROM database_asteroid WHERE closeApproachDate <= date('now','-1 day')")
    fun deleteYesterdayData()
}


@Database(entities = [AsteroidModel::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

//Get the database instance for database opreation
fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AsteroidDatabase::class.java,
                    "videos").build()
        }
    }
    return INSTANCE
}
