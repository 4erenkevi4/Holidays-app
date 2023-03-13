package com.elinext.holidays.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elinext.holidays.di.EngineSDK
import com.elinext.holidays.features.holidaysApi.apiModule
import com.elinext.holidays.models.Day
import com.elinext.holidays.models.Holiday
import com.elinext.holidays.models.Month
import com.elinext.holidays.models.Year
import com.elinext.holidays.utils.Constants.COUNTRY
import com.elinext.holidays.utils.Constants.HOLIDAY
import com.elinext.holidays.utils.Constants.OFFICE_COUNTRY
import com.elinext.holidays.utils.Constants.OFFICE_ID
import com.elinext.holidays.utils.Constants.WEEK_STARTS_ON_MONDAY
import com.elinext.holidays.utils.Constants.WORKING_WEEKEND
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class HolidaysViewModel : ViewModel() {

    private val calendar: Calendar = Calendar.getInstance()

    private val filteredMapOfHolidays = HashMap<Int, List<Holiday>?>()

    private val _curentYear = Channel<Int>()
    val curentYear: Flow<Int> = _curentYear.receiveAsFlow()

    private val _quantityWorkingDaysInYear = Channel<Int>()
    val quantityWorkingDaysInYear: Flow<Int> = _quantityWorkingDaysInYear.receiveAsFlow()

    private val _listOfCountries = Channel<MutableList<String>>()
    val listOfCountries: Flow<MutableList<String>> = _listOfCountries.receiveAsFlow()

    private val _listOfHolidaysLiveData = MutableLiveData<Month>()
    val listOfHolidaysLiveData: LiveData<Month> = _listOfHolidaysLiveData


    private val _listOfMonthLiveData = MutableLiveData<List<Holiday>?>()
    val listOfMonthLiveData: LiveData<List<Holiday>?> = _listOfMonthLiveData

    private val _allHolidaysMapLivedata = MutableLiveData<MutableMap<Int, List<Holiday>?>>()
    val allHolidaysMapLivedata: LiveData<MutableMap<Int, List<Holiday>?>> = _allHolidaysMapLivedata

    private val _holidaysLiveData = MutableLiveData<List<Holiday>>()
    val holidaysLiveData: LiveData<List<Holiday>> = _holidaysLiveData


    fun setYearInTearFragment(year: Int){
        viewModelScope.launch {
            _curentYear.send(year)
        }
    }

    fun savePreferences(context: Context, country: String, officeId: String) {
        val sf: SharedPreferences = context.getSharedPreferences(COUNTRY, 0) ?: return
        val editor = sf.edit()
        editor.putString(OFFICE_COUNTRY, country)
        editor.putString(OFFICE_ID, officeId)
        editor.apply()
    }

    fun getOfficeIdInPreferences(context: Context, ifNeedNameOffice: Boolean = true): String? {
        val sf: SharedPreferences = context.getSharedPreferences(COUNTRY, 0)
        return if (ifNeedNameOffice)
            sf.getString(OFFICE_COUNTRY, getDeviceCountry(context))
        else
            sf.getString(OFFICE_ID, "1")
    }

    private fun getDeviceCountry(context: Context): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (tm.networkCountryIso ?: "bl") {
            "bl" -> "Belarus"
            "ge" -> "Georgia"
            "kz" -> "Kazakhstan"
            "pl" -> "Poland"
            "ua" -> "Ukraine"
            "uz" -> "Uzbekistan"
            "vn" -> "Vietnam"
            else -> "Belarus"
        }
    }


    fun initListOfCountries() {
        viewModelScope.launch {
            val listCountries = mutableListOf<String>()
            Log.d("ktor", "getCountries()")
            EngineSDK.apiModule.holidaysRepository.getCountries().forEach {
                listCountries.add(it.name)
            }
            _listOfCountries.send(listCountries)
        }
    }

    fun getQuantityWorkingDays(year: String, id: String) {
        viewModelScope.launch {
            Log.d("ktor", "get QuantityWorkingDays")
            EngineSDK.apiModule.holidaysRepository.getQuantityWorkingDays(year, id).let {
                _quantityWorkingDaysInYear.send(it.toInt())
            }
        }
    }

    fun getHolidays(context: Context, year: Int? = null) {
        viewModelScope.launch {
            Log.d("ktor", "get AllDays")
            EngineSDK.apiModule.holidaysRepository.getAllDays()?.years?.let {
                calendar.time = Date()
                val sortedYears = it.keys.sorted()
                val upcomingHolidays = arrayListOf<Holiday>()
                sortedYears.forEach { yearInSortedYears ->
                    val filteredHolidaysMapByOffice =
                        it[yearInSortedYears]?.filter { holiday ->
                            holiday.country.countryName == getOfficeIdInPreferences(context)
                        }
                    filteredMapOfHolidays[yearInSortedYears] = filteredHolidaysMapByOffice
                    if (yearInSortedYears == year) {
                        filteredHolidaysMapByOffice?.let { holidays ->
                            upcomingHolidays.addAll(holidays)
                        }
                    }
                }
                if (year != null)
                    _listOfMonthLiveData.value = filteredMapOfHolidays[year]
                else
                    _allHolidaysMapLivedata.value = filteredMapOfHolidays

            }
        }
    }

    fun getDaysOfMonth(
        month: Int,
        year: Int,
    ): MutableList<Day?> {
        calendar.set(year, month, calendar.getMinimum(Calendar.DATE))
        val daysOfWeek = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val listOfDays = arrayListOf<Day?>()
        listOfDays.addAll(getEmptyDaysList(calendar.get(Calendar.DAY_OF_WEEK)))
        for (day in calendar.getMinimum(Calendar.DATE)..daysOfWeek) {
            listOfDays.add(
                Day(
                    day, month, year,
                    getFullDate(day, month, year),
                    holidayCheck(
                        day, month, year
                    ), addComment(getFullDate(day, month, year), year)
                )
            )
        }
        return listOfDays
    }

    fun getWorkingDaysOfMonth(
        year: Int,
        month: Int,
    ): Int {
        calendar.set(year, month, calendar.getMinimum(Calendar.DATE))
        val daysOfWeek = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val listOfDays = arrayListOf<Day?>()
        listOfDays.addAll(getEmptyDaysList(calendar.get(Calendar.DAY_OF_WEEK)))
        for (day in calendar.getMinimum(Calendar.DATE)..daysOfWeek) {
            listOfDays.add(
                Day(
                    day, month, year,
                    getFullDate(day, month, year),
                    holidayCheck(
                        day, month, year
                    ), addComment(getFullDate(day, month, year), year)
                )
            )
        }
        return listOfDays.filter { it?.isHoliday == false }.size
    }

    private fun addComment(fullDate: String, year: Int): String? {
        val isContains =
            filteredMapOfHolidays[year]?.find { it.holidayDate.substring(0, 10) == fullDate }
        return isContains?.comment
    }

    private fun getEmptyDaysList(emptyDays: Int): List<Day?> {
        return List(WEEK_STARTS_ON_MONDAY.indexOf(emptyDays)) { null }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getFullDate(day: Int, month: Int, year: Int): String {
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
    }

    fun holidayCheck(
        day: Int, month: Int, year: Int,
    ): Boolean {
        val fullDate = getFullDate(day, month, year)
        calendar.set(year, month, day)
        var ifWeekend =
            calendar.get(Calendar.DAY_OF_WEEK) == 6 || calendar.get(Calendar.DAY_OF_WEEK) == 7
        val days = filteredMapOfHolidays[year]
        val holidays = arrayListOf<Holiday>()
        val transferredWorkingDays = arrayListOf<Holiday>()
        days?.forEach {
            if (it.holidayType == HOLIDAY) {
                holidays.add(it)
            }
            if (it.holidayType == WORKING_WEEKEND) {
                transferredWorkingDays.add(it)
            }
        }
        ifWeekend = if (ifWeekend) {
            !transferredWorkingDays.any { it.holidayDate == fullDate }
        } else {
            holidays.any { it.holidayDate.substring(0, 10) == fullDate }
        }
        return ifWeekend
    }

    private fun Year.getPreviousYear(): Year {
        calendar.set(this.year, Calendar.JANUARY, 1)
        return Year(this.year - 1, getMonthOfYear(this.year - 1))
    }

    private fun Year.getNextYear(): Year {
        calendar.set(this.year, Calendar.JANUARY, 1)
        return Year(this.year + 1, getMonthOfYear(this.year + 1))
    }

    private fun getMonthOfYear(
        year: Int,
    ): MutableList<Month> {
        calendar.set(year, Calendar.JANUARY, 1)
        val listOfMonth = arrayListOf<Month>()
        for (day in Calendar.JANUARY..Calendar.DECEMBER) {
            listOfMonth.add(Month(year, day, getDaysOfMonth(day, year)))
        }
        return listOfMonth
    }

    private fun Month.getPreviousMonth(): Month {
        calendar.set(this.year, this.month, 1)
        return getMonth(this.month - 1)
    }

    private fun Month.getNextMonth(): Month {
        calendar.set(this.year, this.month, 1)
        return getMonth(this.month + 1)
    }

    private fun getMonth(month: Int): Month {
        calendar.set(Calendar.MONTH, month)
        val calendarYear = calendar.get(Calendar.YEAR)
        val calendarMonth = calendar.get(Calendar.MONTH)
        return Month(
            calendarYear, calendarMonth,
            getDaysOfMonth(
                calendarMonth,
                calendarYear
            )
        )
    }

}