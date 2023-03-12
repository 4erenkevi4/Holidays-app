package com.elinext.holidays.android.ui

import androidx.compose.runtime.*
import com.kizitonwose.calendar.compose.CalendarState

interface CalendarViewInterface {


    @Composable
    fun CustomTabs(calendarState: CalendarState){}

    @Composable
    fun CalendarContent(calendarState: CalendarState){}
}