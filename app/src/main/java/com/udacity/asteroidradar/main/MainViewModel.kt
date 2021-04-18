package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.model.AsteroidModel
import com.udacity.asteroidradar.model.asDomainModel
import com.udacity.asteroidradar.repository.VideosRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response


class MainViewModel(application: Application): AndroidViewModel(application) {


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

    private val database = getDatabase(application)
    private val videosRepository = VideosRepository(database)


    init {
        viewModelScope.launch {
            getPictureOfDayAstroid()
        }

        viewModelScope.launch {
            videosRepository.refreshVideos()
        }

    }

   //val asteroids = videosRepository.videos

     suspend fun getPictureOfDayAstroid() {
         viewModelScope.launch {
             //_status.value = MarsApiStatus.LOADING
             try {
                 _properties.value  = NasaApi.retrofitService.getPictureOfDay()



                 //var img = NasaApi.retrofitService.getPictureOfDay()
                 Log.i("picture==","=="+_properties.value)
                 //database.imageDao.insert(_properties.value)
                // _status.value = MarsApiStatus.DONE
             } catch (e: Exception) {
                 //_status.value = MarsApiStatus.ERROR
                 _properties.value = null
             }
         }
     }



    suspend fun getAstroid() {
        viewModelScope.launch {

            NasaApi.retrofitService.getStringResponse().enqueue( object: retrofit2.Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    _astroids.value = ArrayList()
                    Log.i("fail===","=="+t.message)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {

                    Log.i("i===","=="+response.body())
                    val jsonObject = JSONObject(response.body().toString())
                   var data = ArrayList<Asteroid>()
                    var data1 = parseAsteroidsJsonResult(jsonObject)
                    _astroids.value = data
                    Log.i("dat===","==$data")
                   // database.videoDao.insertAll(*data1.asD)
                    Log.i("ggg","g==="+parseAsteroidsJsonResult(jsonObject).size)
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

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}



