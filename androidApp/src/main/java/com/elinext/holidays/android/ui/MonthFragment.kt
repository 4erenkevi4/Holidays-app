package com.elinext.holidays.android.ui

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.*
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import java.io.Serializable
import java.time.DayOfWeek
import java.time.YearMonth

class MonthFragment : BaseFragment() {

    var restoreYear: Int? = null
    var restoreMonth: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        restoreYear = arguments?.getInt("year", 99)
        restoreMonth = arguments?.getInt("month", 99)
        super.onViewCreated(view, savedInstanceState)

    }

    @Composable
    fun state(year: Int, month: Int): CalendarState {
        val currentMonth = remember { YearMonth.of(year, month) }
        val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
        val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
        val calendarState = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = DayOfWeek.MONDAY
        )
        return calendarState
    }

    @Composable
    override fun CalendarContent(calendarState: CalendarState) {
        val state = if (restoreYear == 99 && restoreMonth == 99) calendarState else state(
            year = restoreYear!!,
            month = restoreMonth!!
        )
        DaysOfWeekTitle(daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY))
        HorizontalCalendar(
            state = state,
            dayContent = { Day(it) }
        )
        HolidaysView(state)
    }
}