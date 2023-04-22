package com.elinext.holidays.android.ui

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.elinext.holidays.android.HolidaysViewModel
import com.elinext.holidays.android.MainActivity
import com.elinext.holidays.android.R
import com.elinext.holidays.models.ApiErrorModel
import com.elinext.holidays.models.Day
import com.elinext.holidays.models.Holiday
import com.elinext.holidays.utils.Constants
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.MonthDay
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Suppress("IMPLICIT_CAST_TO_ANY")
abstract class BaseFragment : Fragment(), CalendarViewInterface {


    val viewModel: HolidaysViewModel by viewModels()
    var allYearsMap: MutableMap<Int, List<Holiday>?> = mutableMapOf()
    var errorDialog: AlertDialog? = null
    var progressDialog: Dialog? = null
    private var toast: Toast? = null


    private fun showErrorPopup(errorModel: ApiErrorModel) {
        val context = context ?: return
        if (errorDialog != null) return
        val builder = AlertDialog.Builder(context)
        errorDialog = builder.create()
        builder.setTitle("Application encountered an error")
        builder.setMessage(errorModel.message)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
            errorDialog = null
            if (errorModel.message.contains("To use the Elinext Holidays app")) {
                (activity as MainActivity?)?.finishAffinity()
            } else {
                (activity as MainActivity?)?.onBackPressed()
            }
        }
        errorDialog = builder.create()
        errorDialog?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        errorDialog?.dismiss()
        errorDialog = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initProgressBar()
        viewModel.errorLivedata.observe(viewLifecycleOwner) {
            showErrorPopup(it)
        }
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            viewModel.allHolidaysMapFlow.collect() {
                allYearsMap = it
            }
        }

        view.findViewById<ComposeView>(R.id.compose_view).setContent {
            GreetingView()
        }

    }

    private fun initProgressBar() {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setView(R.layout.progressbar)
        progressDialog = builder.create()
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
                            findNavController().navigate(
                                R.id.action_global_monthFragment
                            )
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
                .background(Color.White)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CalendarContent(calendarState)
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
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        findNavController().navigate(R.id.action_global_settingsFragment)
                    },
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
            Row(modifier = Modifier.background(MaterialTheme.colors.background).clickable { expanded = !expanded }) {
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
                    modifier = Modifier.background(MaterialTheme.colors.background),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    countries.forEach { label ->
                        DropdownMenuItem(
                            modifier = Modifier.background(MaterialTheme.colors.background),
                            onClick = {
                            vibrate()
                            expanded = false
                            country = label
                            viewModel.savePreferences(
                                context,
                                country!!,
                                countries.indexOf(country).toString()
                            )
                            lifecycleScope.launch {
                                viewModel.getHolidays(
                                    context,
                                    Calendar.getInstance().get(Calendar.YEAR)
                                )
                            }
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

    private fun vibrate() {
        val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val canVibrate: Boolean = vibrator.hasVibrator()
        if (canVibrate) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    10L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
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
                .clickable {
                    dayClickAction("Today - ${day.date.dayOfWeek.name}, ${day.date.dayOfMonth} ${day.date.month.name}")
                }
            else if (holidayInfo != null) {
                if (holidayInfo.holidayType == Constants.HOLIDAY) {
                    Modifier
                        .drawBehind {
                            drawCircle(
                                color = Color.Red.copy(alpha = 0.05f),
                                radius = this.size.maxDimension
                            )
                        }
                        .clickable { dayClickAction(holidayInfo.comment) }
                } else {
                    Modifier
                        .drawBehind {
                            drawCircle(
                                color = Color.Blue.copy(alpha = 0.05f),
                                radius = this.size.maxDimension
                            )
                        }
                        .clickable {
                            dayClickAction(holidayInfo.comment)
                        }
                }

            } else Modifier.clickable {
                dayClickAction(" ${day.date.dayOfWeek.name}, ${day.date.dayOfMonth} ${day.date.month.name}")
            }

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

    private fun dayClickAction(comment: String) {
        toast?.cancel()
        toast = Toast.makeText(context, comment, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        }
        toast?.show()
        vibrate()
    }


    fun formattedData(dateString: String): String? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateTime =
            LocalDateTime.parse(
                dateString.substring(0, 19),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            )
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
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US),
                    color = if (dayOfWeek.name == DayOfWeek.SATURDAY.name || dayOfWeek.name == DayOfWeek.SUNDAY.name) Color.Red else Color.Black
                )
            }
        }
    }

    @Composable
    open fun InfoView(calendarState: CalendarState?) {
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
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp),
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

    @Composable
    fun CircularProgressBar(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colors.primaryVariant,
        strokeWidth: Dp = 4.dp,
        visible: Boolean = true
    ) {
        if (visible) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize().background(Color.White)
            ) {
                CircularProgressIndicator(
                    color = color,
                    strokeWidth = strokeWidth,
                    modifier = Modifier.size(40.dp),
                )
            }
        }
    }

}