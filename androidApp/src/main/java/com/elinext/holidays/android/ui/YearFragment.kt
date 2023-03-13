package com.elinext.holidays.android.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import com.elinext.holidays.android.MyApplicationTheme
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
    override fun GreetingView() {
        val currentYear = Year.now().value
        val year = viewModel.curentYear.collectAsState(initial = currentYear)
        MyApplicationTheme {
            val currentMonth = remember { YearMonth.now() }
            val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
            val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
            val firstDayOfWeek =
                remember { firstDayOfWeekFromLocale() } // Available from the library
            val calendarState = rememberCalendarState(
                startMonth = startMonth,
                endMonth = endMonth,
                firstVisibleMonth = currentMonth,
                firstDayOfWeek = firstDayOfWeek
            )
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = { TopBar(year.value.toString()) },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.White
            ) { value ->
                val padding = value
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CustomTabs(calendarState)
                    }
                }
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
        InfoView(null)
        TestRow(allYearsMap.keys.toList())
        HolidaysView(null)
    }

    @Composable
    override fun HolidaysView(calendarState: CalendarState?) {
        val currentYear = Year.now().value
        val year = viewModel.curentYear.collectAsState(initial = currentYear)
        allYearsMap[year.value]?.let {listHolidays->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "holidays",
                color = MaterialTheme.colors.primaryVariant
            )
            listHolidays.let { holiday ->
                LazyColumn(Modifier.height(if (listHolidays.size > 1) 500.dp else 150.dp)) {
                    itemsIndexed(holiday) { _, currentHoliday ->
                        currentHoliday.let {
                            HolidayItem(com.elinext.holidays.models.Day(1,2,3,it.holidayDate,true,it.comment))
                        }
                    }
                }
            }
        }
        }
    }

    @Composable
    override fun InfoView(calendarState: CalendarState?) {
        val context = context ?: return
        val currentYear = Year.now().value
        val year = viewModel.curentYear.collectAsState(initial = currentYear)
        val oficeId = viewModel.getOfficeIdInPreferences(context, false)
        viewModel.getQuantityWorkingDays(year.value.toString(), oficeId ?: "1")
        val number = viewModel.quantityWorkingDaysInYear.collectAsState(initial = "0")
        var text = "${number.value} working days"
        OutlinedTextField(
            modifier = Modifier.padding(16.dp),
            value = text,
            onValueChange = { text = it },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            label = {
                Text(
                    "${year.value} year info:",
                    color = MaterialTheme.colors.primaryVariant,
                    fontSize = 14.sp
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black,
                unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                focusedBorderColor = MaterialTheme.colors.primaryVariant
            )
        )
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
            flingBehavior = flingBehavior(true, lazyListState),
        ) {
            itemsIndexed(items) { index, item ->
                viewModel.setYearInTearFragment(item)
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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun pagedFlingBehavior(state: LazyListState): FlingBehavior {
        val snappingLayout = remember(state) {
            SnapLayoutInfoProvider(state) { _, _ -> 0f }
        }
        return rememberSnapFlingBehavior(snappingLayout)
    }

    @Composable
    private fun continuousFlingBehavior(): FlingBehavior = ScrollableDefaults.flingBehavior()

    @Composable
    fun flingBehavior(isPaged: Boolean, state: LazyListState): FlingBehavior {
        return if (isPaged) pagedFlingBehavior(state) else continuousFlingBehavior()
    }
}