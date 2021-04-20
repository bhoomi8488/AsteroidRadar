package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Internally, we use a MutableLiveData, because we will be updating the picture of day
    // with new values
    private val _properties = MutableLiveData<PictureOfDay>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<PictureOfDay>
        get() = _properties

    // Internally, we use a MutableLiveData to handle navigation to the selected propert
    private val _navigateToSelectedProperty = MutableLiveData<Asteroid>()

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    var asteroidList = asteroidRepository.asteroid

    init {
        viewModelScope.launch {
            getPictureOfDayAstroid()
            asteroidRepository.refreshAsteroid()
        }
    }

    suspend fun getPictureOfDayAstroid() {
        viewModelScope.launch {
            try {
                _properties.value = NasaApi.retrofitService.getPictureOfDay(Constants.API_KEY)
            } catch (e: Exception) {
                _properties.value = null
            }
        }
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
                @Suppress("UNCHECKED_CAST") return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
    fun AsteroidWeek(){
        asteroidList = asteroidRepository.asteroidWeek
    }
    fun AsteroidToday(){
        asteroidList = asteroidRepository.asteroidToday
    }
    fun AsteroidAll(){
        asteroidList = asteroidRepository.asteroid
    }
}



