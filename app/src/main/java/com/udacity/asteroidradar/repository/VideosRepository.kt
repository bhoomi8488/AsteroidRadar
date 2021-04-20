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

package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.getEndDate
import com.udacity.asteroidradar.api.getStartDate
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.model.asDatabaseModel
import com.udacity.asteroidradar.model.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AsteroidRepository(private val database: AsteroidDatabase) {

    /**
     *
     * A list of asteroids that can be shown on the screen.
     */
    val asteroid: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroid()) {
        it.asDomainModel()
    }

    /**
     *
     * A list of asteroids that can be shown on the screen for current date.
     */
    val asteroidToday: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getTodaysAsteroid()) {
            it.asDomainModel()
        }
    /**
     *
     * A list of asteroids that can be shown on the screen for the week.
     */
    val asteroidWeek: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getWeekAsteroid()) {
            it.asDomainModel()
        }

    /**
     * Refresh the Asteroid stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroid for use, observe [asteroids]
     */

    suspend fun refreshAsteroid() {
        val call = NasaApi.retrofitService.getAsteroidData(
            getStartDate(),
            getEndDate(),
            Constants.API_KEY
        )
        val response = call.awaitResponse()

        val data = parseAsteroidsJsonResult(JSONObject(response.body().toString()))
        if (data != null) {
            withContext(Dispatchers.IO) {
                database.asteroidDao.insertAll(*data.asDatabaseModel())
            }
        }
    }

    suspend fun <T> Call<T>.awaitResponse(): Response<T> {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                cancel()
            }
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    continuation.resume(response)
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    suspend fun deleteYesterdayAsteroid() {
            withContext(Dispatchers.IO) {
               database.asteroidDao.deleteYesterdayData()
                println("Delete yesterday Record")
            }
    }
}


