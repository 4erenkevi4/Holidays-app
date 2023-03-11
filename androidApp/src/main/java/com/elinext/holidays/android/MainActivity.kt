package com.elinext.holidays.android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elinext.holidays.android.ui.YearScreen
import com.elinext.holidays.di.Configuration
import com.elinext.holidays.di.EngineSDK
import com.elinext.holidays.di.PlatformType
import com.elinext.holidays.models.Day
import com.elinext.holidays.models.Holiday
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.MonthDay
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class MainActivity : ComponentActivity() {

    private var listOfHolidays: List<Holiday>? = null

    val viewModel: HolidaysViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = baseContext ?: return
        EngineSDK.init(
            configuration = Configuration(
                platformType = PlatformType.Android("1.0", "1")
            )
        )
        viewModel.initListOfCountries()
        viewModel.getHolidays(context, Calendar.getInstance().get(Calendar.YEAR))
        viewModel.listOfMonthLiveData.observe(this) {
            listOfHolidays = it
            setContent {
                MyApplicationTheme {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        topBar = { TopBar("March , 2023") },
                        backgroundColor = colors.background,
                        contentColor = Color.White
                    ) { value ->
                        val padding = value
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            GreetingView()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TopBar(title: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = title,
                fontSize = 20.sp,
                color = colors.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            DropDownMenu()
            Spacer(modifier = Modifier.weight(1f))
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
        var expanded by remember { mutableStateOf(false) }
        val listCountries = viewModel.listOfCountries.collectAsState(initial = null)

        listCountries.value?.let { countries ->
            Row(modifier = Modifier.clickable { expanded = !expanded }) {
                Text(
                    countries.first(), color = colors.onSurface
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = Red
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                countries.forEach { label ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        //do something ...
                    }) {
                        Text(
                            text = label, color = colors.onSurface
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun CustomTabs(calendarState: CalendarState) {
        var selectedIndex by remember { mutableStateOf(0) }

        val list = listOf("Month", "Year")
        TabRow(selectedTabIndex = selectedIndex,
            backgroundColor = Color.Gray,
            indicator = { tabPositions: List<TabPosition> ->
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
                            colors.background
                        ),
                    selected = selected,
                    onClick = { selectedIndex = index },
                    text = {
                        Text(
                            text = text,
                            color = if (selected) colors.primaryVariant else Color.Gray
                        )
                    }
                )
            }
        }
        if (selectedIndex == 0) {
            InfoView()
            DaysOfWeekTitle(daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY))
            HorizontalCalendar(
                state = calendarState,
                dayContent = { Day(it) }
            )
            HolidaysView()
        } else {
            InfoView()
            YearScreen()
        }
    }

    @Composable
    fun GreetingView() {
        MainScreen()
    }


    @Composable
    fun MainScreen() {
        val currentMonth = remember { YearMonth.now() }
        val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
        val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
        val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library
        val calendarState = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = firstDayOfWeek
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CustomTabs(calendarState)
        }
    }

    @Composable
    fun InfoView() {
        var text = "20 working days (160 working hours)"
        OutlinedTextField(
            modifier = Modifier.padding(16.dp),
            value = text,
            onValueChange = { text = it },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            label = { Text("Info", color = colors.primaryVariant, fontSize = 14.sp) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Black,
                unfocusedBorderColor = colors.primaryVariant,
                focusedBorderColor = colors.primaryVariant
            )
        )
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
                    color = if (dayOfWeek.name == DayOfWeek.SATURDAY.name || dayOfWeek.name == DayOfWeek.SUNDAY.name) Red else Black
                )
            }
        }
    }

    @Composable
    fun Day(day: CalendarDay) {
        val today = MonthDay.now()
        val isDayShownMonth = day.position.name == DayPosition.MonthDate.name
        val isCurrentMonth = (day.date.month.name == today.month.name && isDayShownMonth)

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
            else if(holiday!=null){
                Modifier.clickable {
                    Toast.makeText(
                        this@MainActivity,
                        holiday.comment,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else Modifier

        val color: Color = if (holiday?.holidayType == "HOLIDAY") {
            Red
        }
        else if (isTodayDay) {
            White
        }
        else if (isDayShownMonth) {
            Black
        }
        else
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
    fun HolidaysView() {
        val listHolidays = listOf(
            Day(8, 3, 2023, "8 March 2023", true, "International woman's day"),
            Day(17, 3, 2023, "17 March 2023", true, "Next some holiday")
        )
        Column(
            modifier = Modifier
                .background(colors.background)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "holidays",
                color = colors.primaryVariant
            )
            listHolidays.let { holiday ->
                LazyColumn {
                    itemsIndexed(holiday) { index, currentHoliday ->
                        HolidayItem(currentHoliday)
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
                                .size(6.dp)
                                .background(Red)
                        ) {
                        }
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            text = holiday.date,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = holiday.description!!,
                        modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 20.dp),
                        fontSize = 16.sp,
                        color = Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }


    @Preview
    @Composable
    fun DefaultPreview() {
        MyApplicationTheme {
            GreetingView()
        }
    }
}

