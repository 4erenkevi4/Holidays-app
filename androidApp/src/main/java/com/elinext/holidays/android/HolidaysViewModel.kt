package com.elinext.holidays.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elinext.holidays.di.EngineSDK
import com.elinext.holidays.features.holidaysApi.apiModule
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HolidaysViewModel : ViewModel() {

    private val _listOfCountries = Channel<MutableList<String>>()
    val listOfCountries: Flow<MutableList<String>> = _listOfCountries.receiveAsFlow()


    fun initListOfCountries() {
        viewModelScope.launch {
            val listCountries = mutableListOf<String>()
            print("запрос пошел")

            EngineSDK.apiModule.holidaysRepository.getCountries().forEach {
                listCountries.add(it.name)
            }
            print("запрос заокнчен")
            _listOfCountries.send(listCountries)
        }
    }
}