package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel : ViewModel() {


    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _properties = MutableLiveData<PictureOfDay>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<PictureOfDay>
        get() = _properties


    private val _astroids = MutableLiveData<List<Asteroid>>()

    val asteroids: LiveData<List<Asteroid>>
        get() = _astroids

    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedProperty: LiveData<Asteroid>
        get() = _navigateToSelectedProperty

    init {
        //getPictureOfDayAstroid()
        getAstroid()
    }

    /* private fun getPictureOfDayAstroid() {
         viewModelScope.launch {
             //_status.value = MarsApiStatus.LOADING
             try {
                 _properties.value = NasaApi.retrofitService.getPictureOfDay()
                 Log.i("picture==","=="+_properties.value)
                // _status.value = MarsApiStatus.DONE
             } catch (e: Exception) {
                 //_status.value = MarsApiStatus.ERROR
                 _properties.value = null
             }
         }
     }*/


    private fun getAstroid() {
        viewModelScope.launch {
            NasaApi.retrofitService.getJson().enqueue(object : Callback<JSONObject> {
                override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                   Log.i("fail==","=="+t.message)
                }

                override fun onResponse(call: Call<JSONObject>, response: Response<JSONObject>) {
                    _astroids.value = parseAsteroidsJsonResult((response.body()))
                    Log.i("VALUE====", "===" + _astroids.value)
                }


            })
        }
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param marsProperty The [MarsProperty] that was clicked on.
     */
    fun displayPropertyDetails(asteroid: Asteroid) {
        _navigateToSelectedProperty.value = asteroid
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

}



