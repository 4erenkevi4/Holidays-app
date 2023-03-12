package com.elinext.holidays.android.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elinext.holidays.android.R
import com.elinext.holidays.models.Holiday
import com.elinext.holidays.utils.Constants
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
import com.kizitonwose.calendar.compose.CalendarState
import kotlinx.coroutines.launch

class YearFragment : BaseFragment() {


    var allYearsMap: MutableMap<Int, List<Holiday>?> = mutableMapOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = context ?: return
        viewModel.initListOfCountries()
        viewModel.getHolidays(context)
        viewModel.allHolidaysMapLivedata.observe(viewLifecycleOwner) {
            allYearsMap = it
            view.findViewById<ComposeView>(R.id.compose_view).setContent {
                GreetingView()
            }
        }
    }


    @Composable
    override fun CalendarContent(calendarState: CalendarState) {
        val currentMonth = remember { YearMonth.now() }
        val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
        val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
        val firstDayOfWeek =
            remember { firstDayOfWeekFromLocale() } // Available from the library
        val yearCalendarState = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = firstDayOfWeek
        )
        InfoView(yearCalendarState)
        TestRow(allYearsMap.keys.toList())
        HolidaysView(yearCalendarState)
    }

    @Composable
    fun TestRow(items: List<Int>) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
//

        LazyRow(
            modifier = Modifier
                .width(screenWidth),
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
        ) {
            itemsIndexed(items) { index, item ->
                YearScreen(item)
            }
        }
    }


    @Composable
    fun YearScreen(year: Int) {
        val currentYear = Year.now().value
        val list = arrayListOf<CalendarState>()
        for (i in 1..12) {
            val state = getState(year, i)
            list.add(state)
        }
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        LazyVerticalGrid(
            modifier = Modifier
                .width(screenWidth)
                .height(500.dp),
            columns = GridCells.Fixed(3),
        ) {
            items(list.size) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .width(screenWidth / 3)
                    .padding(horizontal = 2.dp)
                    .clickable {
                    }
                    .clip(shape = RoundedCornerShape(4.dp))
                    .aspectRatio(1f)
                    .border(
                        border = BorderStroke(
                            color = MaterialTheme.colors.background,
                            width = 1.dp
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ))
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
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        .toString(),
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
        val isDayShownMonth = day.position.name == DayPosition.MonthDate.name
        val isCurrentMonth = (day.date.month.name == today.month.name && isDayShownMonth)
        val isHoliday =
            viewModel.holidayCheck(day.date.dayOfMonth, day.date.monthValue - 1, day.date.year)
        val isTodayDay = today.dayOfMonth == day.date.dayOfMonth && isCurrentMonth
        val holiday = listOfHolidays?.firstOrNull {
            formattedData(it.holidayDate) == "${day.date}"
        }

        val modifierForDay =
            if (isTodayDay) Modifier
                .drawBehind {
                    drawCircle(
                        color = Color.Gray,
                        radius = this.size.maxDimension
                    )
                }
            else if (holiday != null) {
                Modifier.clickable {
                    Toast.makeText(
                        context,
                        holiday.comment,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else Modifier

        val color: Color = if (isTodayDay) {
            Color.White
        } else if (holiday?.holidayType == Constants.WORKING_WEEKEND) {
            Color.Blue
        } else if (isHoliday) {
            Color.Red
        } else if (isDayShownMonth) {
            Color.Black
        } else
            Color.Gray

        Box(
            modifier = Modifier
                .aspectRatio(1f), // This is important for square sizing!
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = modifierForDay,
                text = day.date.dayOfMonth.toString(),
                color = color,
                fontSize = 8.sp
            )
        }
    }

    @Composable
    fun getState(yearValue: Int, monthValue: Int): CalendarState {
        val firstMonth = remember { YearMonth.of(yearValue, monthValue) }
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
}