package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url


/**
 * Created by Bhoomi on 4/14/2021.
 */
enum class MarsApiFilter(val value: String) { SHOW_IMAGE("image") }
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
     * The @GET annotation indicates that the "realestate" endpoint will be requested with the GET
     * HTTP method
     */
    @GET(value = "planetary/apod?api_key=fUa738pdYocTUykPvL39AcOWOEib4NlbCnRRmsFl")
    suspend fun getPictureOfDay( ): PictureOfDay

    @GET(value = "neo/rest/v1/feed?start_date=2021-04-17&end_date=2021-04-24&api_key=fUa738pdYocTUykPvL39AcOWOEib4NlbCnRRmsFl")
    fun getStringResponse(): Call<String>

   /* @GET(value = "neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=fUa738pdYocTUykPvL39AcOWOEib4NlbCnRRmsFl")
    suspend fun getJson(): Call<JSONObject>*/

    //suspend fun getAstroid( ): List<Asteroid>
}




/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object NasaApi {
    val retrofitService : NasaApiService by lazy { retrofit.create(NasaApiService::class.java) }
}