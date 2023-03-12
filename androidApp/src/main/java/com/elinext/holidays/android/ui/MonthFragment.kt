package com.elinext.holidays.android.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.elinext.holidays.android.MyApplicationTheme
import com.elinext.holidays.android.R
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.YearMonth
import java.util.*

class MonthFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return
    }

    @Composable
    override fun GreetingView() {
        super.GreetingView()
    }

    @Composable
    override fun CalendarContent(calendarState: CalendarState) {
        InfoView(calendarState)
        DaysOfWeekTitle(daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY))
        HorizontalCalendar(
            state = calendarState,
            dayContent = { Day(it) }
        )
        HolidaysView(calendarState)
    }
}