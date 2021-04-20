package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Bhoomi on 4/14/2021.
 */
/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(Constants.BASE_URL)
        .build()

/**
 * A public interface that exposes the [getProperties] method
 */
interface NasaApiService {
    /**
     * Returns a Coroutine [List] of [Asteroid] which can be fetched with await() if in a Coroutine scope.
     * The @GET annotation indicates that the "planetary/apod" endpoint will be requested with the GET
     * HTTP method
     * Pass the query string for the API_Key
     */
    @GET(value = "planetary/apod")
    suspend fun getPictureOfDay(@Query("api_key") key : String ): PictureOfDay

// Get the Asteroid data from current date to week
    @GET(value = "neo/rest/v1/feed")
    fun getAsteroidData(@Query("start_date") start_date: String, @Query("end_date") end_date: String, @Query("api_key") key : String) :Call<String>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object NasaApi {
    val retrofitService : NasaApiService by lazy { retrofit.create(NasaApiService::class.java) }
}