package com.elinext.holidays.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.elinext.holidays.android.HolidaysViewModel
import com.elinext.holidays.android.MyApplicationTheme
import com.elinext.holidays.android.R
import com.elinext.holidays.models.Day
import com.elinext.holidays.models.Holiday
import com.elinext.holidays.utils.Constants
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.MonthDay
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

abstract class BaseFragment : Fragment(), CalendarViewInterface {


    val viewModel: HolidaysViewModel by viewModels()
    var allYearsMap: MutableMap<Int, List<Holiday>?> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = context ?: return
        viewModel.initListOfCountries()
        if (allYearsMap.isEmpty()) {
            viewModel.getHolidays(context, Calendar.getInstance().get(Calendar.YEAR))
        }
        viewModel.allHolidaysMapLivedata.observe(viewLifecycleOwner) {
            allYearsMap = it
            // listOfHolidays = it
            view.findViewById<ComposeView>(R.id.compose_view).setContent {
                GreetingView()
            }
        }
    }

    @Composable
    override fun CustomTabs(calendarState: CalendarState) {

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
            InfoView(calendarState)
            CalendarContent(calendarState)
        }
    }

    @Composable
    open fun GreetingView() {
        MyApplicationTheme {
            val currentMonth = remember { YearMonth.now() }
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
    fun getTitle(calendarState: CalendarState, onlyMonthName: Boolean = false): String {
        val month = calendarState.firstVisibleMonth
        val monthName = month.yearMonth.month.name
        val formattedMonthName: String =
            monthName.substring(0, 1).uppercase(Locale.ROOT) + monthName.substring(1)
                .lowercase(Locale.ROOT)
        return if (onlyMonthName) "$formattedMonthName info: " else "$formattedMonthName, ${month.yearMonth.year}"
    }

    @Composable
    fun TopBar(title: String?) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (title == null) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            findNavController().navigate(R.id.action_global_monthFragment)
                        },
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    tint = Color.Gray,
                    contentDescription = "back"
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .width(200.dp),
                    text = title,
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.onSurface
                )
            }
            DropDownMenu()
            Icon(
                modifier = Modifier.padding(horizontal = 16.dp),
                painter = painterResource(id = R.drawable.ic_settings_24),
                tint = Color.Gray,
                contentDescription = "settings"
            )
        }
    }

    @Composable
    fun DropDownMenu() {
        val context = context ?: return
        var expanded by remember { mutableStateOf(false) }
        val listCountries = viewModel.listOfCountries.collectAsState(initial = null)
        var country by remember { mutableStateOf(listCountries.value?.first()) }
        val savedCountry = viewModel.getOfficeIdInPreferences(context)

        listCountries.value?.let { countries ->
            Row(modifier = Modifier.clickable { expanded = !expanded }) {
                Text(
                    savedCountry ?: countries.first(), color = MaterialTheme.colors.onSurface
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
            Box() {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    countries.forEach { label ->
                        DropdownMenuItem(onClick = {
                            expanded = false
                            country = label
                            viewModel.savePreferences(
                                context,
                                country!!,
                                countries.indexOf(country).toString()
                            )
                            viewModel.getHolidays(
                                context,
                                Calendar.getInstance().get(Calendar.YEAR)
                            )
                        }) {
                            Text(
                                text = label, color = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Day(day: CalendarDay) {
        val today = MonthDay.now()
        val isDayShownMonth = day.position.name == DayPosition.MonthDate.name
        val isCurrentMonth = (day.date.month.name == today.month.name && isDayShownMonth)
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
                color = color
            )
        }
    }


    fun formattedData(dateString: String): String? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateTime =
            LocalDateTime.parse(dateString.substring(0, 19), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return dateTime.format(formatter).substring(0, 10)

    }

    @Composable
    fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            for (dayOfWeek in daysOfWeek) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    color = if (dayOfWeek.name == DayOfWeek.SATURDAY.name || dayOfWeek.name == DayOfWeek.SUNDAY.name) Color.Red else Color.Black
                )
            }
        }
    }


    @Composable
    open fun InfoView(calendarState: CalendarState?) {
        calendarState?.firstVisibleMonth?.yearMonth?.let { month ->
            val number = viewModel.getWorkingDaysOfMonth(month.year, month.month.value - 1)
            var text = "$number working days (${number * 8} working hours)"
            OutlinedTextField(
                modifier = Modifier.padding(16.dp),
                value = text,
                onValueChange = { text = it },
                readOnly = true,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                label = {
                    Text(
                        getTitle(calendarState = calendarState, true),
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
    }


    @Composable
    open fun HolidaysView(calendarState: CalendarState?) {
        calendarState?.firstVisibleMonth?.yearMonth?.let { month ->
            val listHolidays =
                viewModel.getDaysOfMonth(year = month.year, month = month.monthValue - 1)
                    .filter { it?.description.isNullOrEmpty().not() }
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
                            currentHoliday?.let {
                                HolidayItem(it)
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun HolidayItem(holiday: Day) {
        Box(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = 5.dp,
                backgroundColor = Color.White
            ) {
                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(8.dp)
                                .background(if (holiday.description?.contains("instead") == true) Color.Blue else Color.Red)
                        ) {
                        }
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            text = holiday.date,
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = holiday.description!!,
                        modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 20.dp),
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    @Composable
    fun getState(yearValue: Int, monthValue: Int): CalendarState {
        return rememberCalendarState(
            startMonth = YearMonth.of(yearValue, monthValue),
            firstDayOfWeek = DayOfWeek.MONDAY
        )
    }


}