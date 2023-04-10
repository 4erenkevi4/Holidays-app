package com.elinext.holidays.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.elinext.holidays.android.MyApplicationTheme
import com.elinext.holidays.android.R
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

    var isScrolled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("System.out", "----->${this.javaClass.name}")
        view.findViewById<ComposeView>(R.id.compose_view).setContent {
            GreetingView()
        }
    }



    fun setData() {
        val context = context ?: return
        lifecycleScope.launch {
            this.launch { viewModel.initListOfCountries() }
            this.launch { viewModel.getHolidays(context, Year.now().value) }
            if (allYearsMap.isEmpty()) {
                this.launch {
                    viewModel.getHolidays(
                        context,
                        Calendar.getInstance().get(Calendar.YEAR)
                    )
                }
            }
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    override fun GreetingView() {
        val allYearsState = viewModel.allHolidaysMapFlow.collectAsState(initial = null)
        val lazyListState = rememberLazyListState()
        MyApplicationTheme {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = { TopBar(null) },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.White
            ) { value ->
                val padding = value
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                        if (allYearsState.value == null || allYearsMap.isEmpty()) {
                            CircularProgressBar()
                        } else {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val items = allYearsState.value!!.keys.toList()
                                if (!isScrolled) {
                                    rememberCoroutineScope().launch {
                                        lazyListState.scrollToItem(items.lastIndex)
                                        isScrolled = true

                                    }
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
                                            val state = getState(yearValue = item, monthValue = 1)
                                            InfoView(state)
                                            YearScreen(state)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    override fun HolidaysView(calendarState: CalendarState?) {
        allYearsMap[calendarState?.firstVisibleMonth?.yearMonth?.year]?.let { listHolidays ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
                    .fillMaxSize()
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp)
                        .fillMaxWidth(),
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

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    override fun InfoView(calendarState: CalendarState?) {
        val context = context ?: return
        val oficeId = viewModel.getOfficeIdInPreferences(context, false)
        lifecycleScope.launch {
            viewModel.getQuantityWorkingDays(
                calendarState?.firstVisibleMonth?.yearMonth?.year.toString(),
                oficeId ?: "1"
            )
        }

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
                    "${calendarState?.firstVisibleMonth?.yearMonth?.year} year info:",
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
    fun YearScreen(state: CalendarState) {
        val list = arrayListOf<CalendarState>()
        for (i in 1..12) {
            list.add(getState(state.firstVisibleMonth.yearMonth.year, i))
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
                    .padding(top = 4.dp)
            ) {
                YearCalendarItem(list[3], screenWidth)
                YearCalendarItem(list[4], screenWidth)
                YearCalendarItem(list[5], screenWidth)
            }
            Row(
                modifier = Modifier
                    .width(screenWidth)
                    .padding(top = 4.dp)
            ) {
                YearCalendarItem(list[6], screenWidth)
                YearCalendarItem(list[7], screenWidth)
                YearCalendarItem(list[8], screenWidth)
            }
            Row(
                modifier = Modifier
                    .width(screenWidth)
                    .padding(vertical = 4.dp)
            ) {
                YearCalendarItem(list[9], screenWidth)
                YearCalendarItem(list[10], screenWidth)
                YearCalendarItem(list[11], screenWidth)
            }
            HolidaysView(state)
        }
    }

    private fun getEnterAnim(calendarState: CalendarState?): Int {

        return when (calendarState?.firstVisibleMonth?.yearMonth?.month?.value) {
            1 -> R.anim.fade_out_januar
            2 -> R.anim.fade_out_feb
            3 -> R.anim.fade_out_mar
            4 -> R.anim.fade_out_apr
            5 -> R.anim.fade_out_may
            6 -> R.anim.fade_out_jun
            7 -> R.anim.fade_out_jul
            8 -> R.anim.fade_out_aug
            9 -> R.anim.fade_out_sep
            10 -> R.anim.fade_out_oct
            11 -> R.anim.fade_out_now
            12 -> R.anim.fade_out_dec
            else -> R.anim.fade_out_may
        }
    }

    @Composable
    fun YearCalendarItem(calendarState: CalendarState, screenWidth: Dp) {
        val interactionSource = remember { MutableInteractionSource() }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .width(screenWidth / 3)
            .padding(horizontal = 2.dp)
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                val animResId = getEnterAnim(calendarState)
                findNavController().navigate(
                    R.id.action_global_monthFragment,
                    bundleOf(
                        Pair("year", calendarState.firstVisibleMonth.yearMonth.year),
                        Pair("month", calendarState.firstVisibleMonth.yearMonth.month.value)
                    ),
                    NavOptions
                        .Builder()
                        .setExitAnim(R.anim.fade_out)
                        .setEnterAnim(animResId)
                        .build()
                )

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
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                textAlign = TextAlign.Center,
                text = calendarState.firstVisibleMonth.yearMonth.month.name,
                fontSize = 9.sp,
                color = MaterialTheme.colors.primaryVariant
            )
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
                .padding(top = 1.dp, start = 1.dp, end = 1.dp)
        ) {
            for (dayOfWeek in daysOfWeek) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
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
        val holidayInfo = allYearsMap[day.date.year]?.firstOrNull {
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
            else Modifier

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
                .aspectRatio(1.1f), // This is important for square sizing!
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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun pagedFlingBehavior(state: LazyListState): FlingBehavior {
        val snappingLayout = remember(state) {
            SnapLayoutInfoProvider(state) { _, _ -> 0f }
        }
        return rememberSnapFlingBehavior(snappingLayout)
    }
}