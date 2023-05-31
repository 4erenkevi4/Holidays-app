package com.elinext.holidays.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elinext.holidays.di.EngineSDK
import com.elinext.holidays.features.holidaysApi.apiModule
import com.elinext.holidays.models.*
import com.elinext.holidays.utils.Constants
import com.elinext.holidays.utils.Constants.HOLIDAY
import com.elinext.holidays.utils.Constants.HOLIDAYS_APP
import com.elinext.holidays.utils.Constants.NOTIFICATION_DAY
import com.elinext.holidays.utils.Constants.NOTIFICATION_HOUR
import com.elinext.holidays.utils.Constants.NOTIFICATION_SETTINGS
import com.elinext.holidays.utils.Constants.NOTIFICATION_SP_KEY
import com.elinext.holidays.utils.Constants.OFFICE_COUNTRY
import com.elinext.holidays.utils.Constants.OFFICE_ID
import com.elinext.holidays.utils.Constants.WEEK_STARTS_ON_MONDAY
import com.elinext.holidays.utils.Constants.WORKING_WEEKEND
import com.google.gson.Gson
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import java.text.SimpleDateFormat
import java.util.*


class HolidaysViewModel : ViewModel() {


    private val calendar: Calendar = Calendar.getInstance()

    private val filteredMapOfHolidays = HashMap<Int, List<Holiday>?>()

    private val _quantityWorkingDaysInYear = Channel<Int>()
    val quantityWorkingDaysInYear: Flow<Int> = _quantityWorkingDaysInYear.receiveAsFlow()

    private val _listOfCountries = Channel<MutableList<String>>()
    val listOfCountries: Flow<MutableList<String>> = _listOfCountries.receiveAsFlow()

    val allHolidaysMapFlow = MutableSharedFlow<MutableMap<Int, List<Holiday>?>>()

    private val _upcomingHolidaysLivedata = MutableLiveData<List<Holiday>>()
    val upcomingHolidaysLivedata: LiveData<List<Holiday>> = _upcomingHolidaysLivedata

    private val _errorLivedata = MutableLiveData<ApiErrorModel>()
    val errorLivedata: LiveData<ApiErrorModel> = _errorLivedata

    fun saveNotificationToPreferences(context: Context, country: String, description: String, month: Int, day: Int) {
        val sf: SharedPreferences = context.getSharedPreferences(HOLIDAYS_APP, 0) ?: return
        val editor = sf.edit()
        val notification = Notification(country, description, month, day, Random().nextInt())
        val notificationJson = Gson().toJson(notification)
        editor.putString(NOTIFICATION_SP_KEY+month, notificationJson)
        editor.apply()
    }

    fun getNotificationInPreferences(context: Context, month: Int): Notification? {
        val sf: SharedPreferences = context.getSharedPreferences(HOLIDAYS_APP, 0)
        val notificationString = sf.getString(NOTIFICATION_SP_KEY+month, "")
        if (notificationString.isNullOrEmpty()) return null
        return Gson().fromJson(notificationString, Notification::class.java)
    }

     fun removeNotificationFromSP(context: Context, month: Int) {
        val sf: SharedPreferences = context.getSharedPreferences(Constants.HOLIDAYS_APP, 0) ?: return
        val editor = sf.edit()
        editor.remove(Constants.NOTIFICATION_SP_KEY +month)
        editor.apply()
    }

    fun savePreferences(context: Context, country: String, officeId: String) {
        val sf: SharedPreferences = context.getSharedPreferences(HOLIDAYS_APP, 0) ?: return
        val editor = sf.edit()
        editor.putString(OFFICE_COUNTRY, country)
        editor.putString(OFFICE_ID, officeId)
        editor.apply()
    }

    fun getOfficeIdInPreferences(context: Context, ifNeedNameOffice: Boolean = true): String {
        val sf: SharedPreferences = context.getSharedPreferences(HOLIDAYS_APP, 0)
        return if (ifNeedNameOffice)
            sf.getString(OFFICE_COUNTRY, getDeviceCountry(context))?: "Belarus"
        else
            sf.getString(OFFICE_ID, "1")?:"1"
    }

    fun saveNotificationToSp(context: Context, value: Boolean) {
        val sf: SharedPreferences = context.getSharedPreferences(HOLIDAYS_APP, 0)
        val editor = sf.edit()
        editor.putBoolean(NOTIFICATION_SETTINGS, value)
        editor.apply()
    }

    fun getNotificationFromSp(context: Context): Boolean {
        val sf: SharedPreferences = context.getSharedPreferences(HOLIDAYS_APP, 0)
        return sf.getBoolean(NOTIFICATION_SETTINGS, false)
    }

    fun saveNotificationDateToSp(context: Context, value: Int) {
        val sf: SharedPreferences = context.getSharedPreferences(HOLIDAYS_APP, 0)
        val editor = sf.edit()
        editor.putInt(NOTIFICATION_DAY, value)
        editor.apply()
    }

    fun getNotificationDateFromSp(context: Context): Int {
        val sf: SharedPreferences = context.getSharedPreferences(HOLIDAYS_APP, 0)
        return sf.getInt(NOTIFICATION_DAY, 0)
    }

    fun saveNotificationHourToSp(context: Context, value: Int) {
        val sf: SharedPreferences = context.getSharedPreferences(HOLIDAYS_APP, 0)
        val editor = sf.edit()
        editor.putInt(NOTIFICATION_HOUR, value)
        editor.apply()
    }

    fun getNotificationHourFromSp(context: Context): Int {
        val sf: SharedPreferences = context.getSharedPreferences(HOLIDAYS_APP, 0)
        return sf.getInt(NOTIFICATION_HOUR, 0)
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


    suspend fun initListOfCountries() {
        val listCountries = mutableListOf<String>()
        Log.d("ktor", "getCountries()")
        val result = EngineSDK.apiModule.holidaysRepository.getCountries()
        safeErrorProcessing(result.first)
        result.second?.forEach {
            listCountries.add(it.name)
            _listOfCountries.send(listCountries)
        }
    }

   suspend fun getQuantityWorkingDays(year: String, id: String): Int {
            Log.d("ktor", "get QuantityWorkingDays")
            val result = EngineSDK.apiModule.holidaysRepository.getQuantityWorkingDays(year, id)
            safeErrorProcessing(result.first)
            result.second?.let { _quantityWorkingDaysInYear.send(it.toInt())
            return it.toInt()
            }
       return 0
    }

    suspend fun getHolidays(context: Context, year: Int? = null) {
        Log.d("ktor", "get AllDays")
        val result = EngineSDK.apiModule.holidaysRepository.getAllDays()
        safeErrorProcessing(result.first)
        result.second?.years?.let {
            calendar.time = Date()
            val sortedYears = it.keys.sorted()
            sortedYears.forEach { yearInSortedYears ->
                val filteredHolidaysMapByOffice =
                    it[yearInSortedYears]?.filter { holiday ->
                        holiday.country.countryName == getOfficeIdInPreferences(context)
                    }
                filteredMapOfHolidays[yearInSortedYears] = filteredHolidaysMapByOffice
                if (yearInSortedYears == year) {
                    filteredHolidaysMapByOffice?.let { holidays ->
                        _upcomingHolidaysLivedata.value = holidays
                    }
                }
            }
            allHolidaysMapFlow.emit(filteredMapOfHolidays.toSortedMap())
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


    /**
     * Function for processing exceptions and sending an error signal to the error liveData.
     * @param statusCode Exception object that caused the error
     */
    private fun safeErrorProcessing(statusCode: HttpStatusCode) {
        if (statusCode.isSuccess().not()) {
            _errorLivedata.value = (
                    ApiErrorModel(
                        serverErrorMessage = statusCode.description,
                        responseErrorCode = statusCode.value
                    )
                    )
        }
    }
}
