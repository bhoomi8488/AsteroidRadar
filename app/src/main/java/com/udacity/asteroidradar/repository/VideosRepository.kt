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

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.VideosDatabase
import com.udacity.asteroidradar.model.asDatabaseModel
import com.udacity.asteroidradar.model.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class VideosRepository(private val database: VideosDatabase) {

    /**
     * A playlist of videos that can be shown on the screen.
     */
    val videos: LiveData<List<Asteroid>> =
            Transformations.map(database.videoDao.getAsteroid()) {
        it.asDomainModel()
    }

    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the videos for use, observe [videos]
     */
   suspend fun refreshVideos() {
        withContext(Dispatchers.IO) {
                NasaApi.retrofitService.getStringResponse().enqueue( object: retrofit2.Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                    }
                    override fun onResponse(call: Call<String>, response: Response<String>) {

                        val jsonObject = JSONObject(response.body().toString())
                        var data = parseAsteroidsJsonResult(jsonObject)
                        database.videoDao.insertAll(*data.asDatabaseModel())
                    }
                })

        }
    }
}
