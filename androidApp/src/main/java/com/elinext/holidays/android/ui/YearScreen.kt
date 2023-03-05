package com.elinext.holidays.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.MonthDay
import java.time.Year
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun YearScreen () {
    val list = arrayListOf<CalendarState>()
    for (i in 1..12) {
        val state = getState(Year.now().value, i)
        list.add(state)
    }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp/3
    LazyVerticalGrid(
        columns = GridCells.Fixed(3)
    ) {
        items(list.size) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(screenWidth).padding(horizontal = 2.dp).clickable {
            }
                .clip(shape = RoundedCornerShape(4.dp))
                .aspectRatio(1f)
                .border(
                    border = BorderStroke(color = MaterialTheme.colors.background,
                        width = 1.dp),
                    shape = RoundedCornerShape(4.dp)))
                {
                DaysOfWeek(daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY))
                HorizontalCalendar(
                    state = list[it],
                    calendarScrollPaged = false,
                    userScrollEnabled = false,
                    dayContent = { DayForYear(it) },
                )
            }
        }
    }
}


@Composable
fun DaysOfWeek(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).toString(),
                fontSize = 9.sp,
                maxLines = 1,
                color = if (dayOfWeek.name == DayOfWeek.SATURDAY.name || dayOfWeek.name == DayOfWeek.SUNDAY.name) Color.Red else Color.Black
            )
        }
    }
}


@Composable
fun DayForYear(day: CalendarDay) {
    val today = MonthDay.now()
    val isCurrentMonth = day.position.name == DayPosition.MonthDate.name
    val color: Color = if (today.dayOfMonth == day.date.dayOfMonth && isCurrentMonth) {
        Color.Red
    } else if (isCurrentMonth) {
        Color.Black
    } else
        Color.Gray

    Box(
        modifier = Modifier
            .aspectRatio(1f), // This is important for square sizing!
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = day.date.dayOfMonth.toString(),
            color = color,
            fontSize = 8.sp
        )
    }
}

@Composable
fun getState(yearValue: Int, monthValue: Int): CalendarState {
    val firstMonth = remember { YearMonth.of(yearValue,monthValue) }
    val startMonth = remember { firstMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { firstMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    return rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = firstMonth,
        firstDayOfWeek = firstDayOfWeek
    )
}