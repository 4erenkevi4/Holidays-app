package com.elinext.holidays.android.ui

import androidx.compose.runtime.*
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek

class MonthFragment : BaseFragment() {



    @Composable
    override fun CalendarContent(calendarState: CalendarState, year: State<Int>?) {
        InfoView(calendarState, year)
        DaysOfWeekTitle(daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY))
        HorizontalCalendar(
            state = calendarState,
            dayContent = { Day(it) }
        )
        HolidaysView(calendarState, year)
    }
}