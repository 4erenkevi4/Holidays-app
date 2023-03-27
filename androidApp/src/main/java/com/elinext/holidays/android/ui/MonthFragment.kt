package com.elinext.holidays.android.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.elinext.holidays.android.MyApplicationTheme
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.YearMonth

class MonthFragment : BaseFragment() {

    var restoredYear: Int? = null
        get() = if (field == 99) null else field
        private set
    var restoredMonth: Int? = null
        get() = if (field == 99) null else field
        private set


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        restoredYear = arguments?.getInt("year", 99)
        restoredMonth = arguments?.getInt("month", 99)
        super.onViewCreated(view, savedInstanceState)

    }

    @Composable
    override fun GreetingView() {
        val yearMonth = if (restoredYear != null && restoredMonth != null) YearMonth.of(
            restoredYear!!,
            restoredMonth!!
        ) else YearMonth.now()
        MyApplicationTheme {
            val currentMonth = remember { yearMonth}
            val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
            val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
            val calendarState = rememberCalendarState(
                startMonth = startMonth,
                endMonth = endMonth,
                firstVisibleMonth = currentMonth,
                firstDayOfWeek = DayOfWeek.MONDAY
            )
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    TopBar(getTitle(calendarState))
                },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.White
            ) { value ->
                val padding = value
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // CustomTabs(calendarState)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CustomTabs(calendarState)
                    }
                }
            }
        }
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

        DaysOfWeekTitle(daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY))
        HorizontalCalendar(
            state = calendarState,
            dayContent = { Day(it) }
        )
        HolidaysView(calendarState)
    }
}