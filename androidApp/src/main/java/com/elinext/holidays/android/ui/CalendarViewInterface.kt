package com.elinext.holidays.android.ui

import androidx.compose.runtime.*
import com.kizitonwose.calendar.compose.CalendarState

interface CalendarViewInterface {


    @Composable
    fun CustomTabs(calendarState: CalendarState, year: State<Int>?){}

    @Composable
    fun CalendarContent(calendarState: CalendarState, year: State<Int>?){}
}