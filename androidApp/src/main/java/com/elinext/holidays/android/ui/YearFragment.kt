package com.elinext.holidays.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.findNavController
import com.elinext.holidays.android.MyApplicationTheme
import com.elinext.holidays.android.R
import com.elinext.holidays.models.Holiday
import com.elinext.holidays.utils.Constants
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.MonthDay
import java.time.Year
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

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

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    override fun GreetingView() {
        val currentYear = Year.now().value

        val yearRemember = remember {
            mutableStateOf(currentYear)
        }
        MyApplicationTheme {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = { TopBar(currentYear.toString()) },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.White
            ) { value ->
                val padding = value
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // CustomTabs(calendarState)
                        val id =
                            if (findNavController().currentDestination?.label == MonthFragment::class.simpleName) 0 else 1
                        var selectedIndex by remember { mutableStateOf(id) }

                        val list = listOf("Month", "Year")
                        TabRow(selectedTabIndex = selectedIndex,
                            backgroundColor = Color.Gray,
                            indicator = {
                                Box {}
                            }
                        ) {
                            list.forEachIndexed { index, text ->
                                val selected = selectedIndex == index
                                Tab(
                                    modifier = if (selected) Modifier
                                        .background(
                                            Color.White
                                        )
                                    else Modifier
                                        .background(
                                            MaterialTheme.colors.background
                                        ),
                                    selected = selected,
                                    onClick = {
                                        selectedIndex = index
                                        if (selectedIndex == 0) {
                                            findNavController().navigate(R.id.action_global_monthFragment)
                                        } else {
                                            findNavController().navigate(R.id.action_global_yearFragment)
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = text,
                                            color = if (selected) MaterialTheme.colors.primaryVariant else Color.Gray
                                        )
                                    }
                                )
                            }
                        }
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            InfoView(null, yearRemember)
                            val items = allYearsMap.keys.toList()
                            val lazyListState = rememberLazyListState()
                            rememberCoroutineScope().launch {
                                lazyListState.scrollToItem(items.lastIndex)
                            }
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                state = lazyListState,
                                horizontalArrangement = Arrangement.spacedBy(0.dp),
                                flingBehavior = pagedFlingBehavior(lazyListState),
                            ) {
                                itemsIndexed(items) { index, item ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        YearScreen(item)
                                        //yearState.value = item
                                        yearRemember.value = item
                                        //viewModel.setYearInTearFragment(item)
                                    }

                                }
                            }
                            HolidaysView(calendarState = null)
                        }
                    }
                }
            }
        }
    }


    @Composable
    override fun HolidaysView(calendarState: CalendarState?) {
        allYearsMap[1]?.let { listHolidays ->
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
                                HolidayItem(
                                    com.elinext.holidays.models.Day(
                                        1,
                                        2,
                                        3,
                                        it.holidayDate,
                                        true,
                                        it.comment
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    override fun InfoView(calendarState: CalendarState?, yearRemember: MutableState<Int>?) {
        val context = context ?: return
        val oficeId = viewModel.getOfficeIdInPreferences(context, false)
        viewModel.getQuantityWorkingDays(yearRemember?.value.toString(), oficeId ?: "1")
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
                    "${yearRemember?.value.toString()} year info:",
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

    @SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
    @Composable
    fun TestRow() {


    }


    @Composable
    fun YearScreen(year: Int) {
        val list = arrayListOf<CalendarState>()
        for (i in 1..12) {
            list.add(getState(year, i))
        }
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        Column(
            modifier = Modifier
                .width(screenWidth)
        ) {
            Row(
                modifier = Modifier
                    .width(screenWidth)
            ) {
                YearCalendarItem(list[0], screenWidth)
                YearCalendarItem(list[1], screenWidth)
                YearCalendarItem(list[2], screenWidth)
            }
            Row(
                modifier = Modifier
                    .width(screenWidth)
            ) {
                YearCalendarItem(list[3], screenWidth)
                YearCalendarItem(list[4], screenWidth)
                YearCalendarItem(list[5], screenWidth)
            }
            Row(
                modifier = Modifier
                    .width(screenWidth)
            ) {
                YearCalendarItem(list[6], screenWidth)
                YearCalendarItem(list[7], screenWidth)
                YearCalendarItem(list[8], screenWidth)
            }
            Row(
                modifier = Modifier
                    .width(screenWidth)
            ) {
                YearCalendarItem(list[9], screenWidth)
                YearCalendarItem(list[10], screenWidth)
                YearCalendarItem(list[11], screenWidth)
            }
        }
    }


    @Composable
    fun YearCalendarItem(calendarState: CalendarState, screenWidth: Dp) {
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
                state = calendarState,
                calendarScrollPaged = false,
                userScrollEnabled = false,
                dayContent = { DayForYear(it) },
            )
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
        if (day.position != DayPosition.MonthDate) return
        val today = MonthDay.now()
        val isDayShownMonth = day.position.name == DayPosition.MonthDate.name
        val isCurrentMonth =
            (day.date.month.name == today.month.name && isDayShownMonth) && (day.date.year == Year.now().value)
        val isTodayDay = today.dayOfMonth == day.date.dayOfMonth && isCurrentMonth
        val holidayInfo = listOfHolidays?.firstOrNull {
            formattedData(it.holidayDate) == "${day.date}"
        }
        val isHoliday =
            day.date.dayOfWeek == DayOfWeek.SUNDAY || day.date.dayOfWeek == DayOfWeek.SATURDAY || holidayInfo?.holidayType == Constants.HOLIDAY


        val modifierForDay =
            if (isTodayDay) Modifier
                .drawBehind {
                    drawCircle(
                        color = Color.Gray,
                        radius = this.size.maxDimension
                    )
                }
            else if (holidayInfo != null) {
                Modifier.clickable {
                    Toast.makeText(
                        context,
                        holidayInfo.comment,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else Modifier

        val color: Color = if (isTodayDay) {
            Color.White
        } else if (holidayInfo?.holidayType == Constants.WORKING_WEEKEND) {
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
    fun getState(yearValue: Int, monthValue: Int): CalendarState = rememberCalendarState(
        startMonth = YearMonth.of(yearValue, monthValue),
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun pagedFlingBehavior(state: LazyListState): FlingBehavior {
        val snappingLayout = remember(state) {
            SnapLayoutInfoProvider(state) { _, _ -> 0f }
        }
        return rememberSnapFlingBehavior(snappingLayout)
    }
}