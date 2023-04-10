package com.elinext.holidays.android.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.elinext.holidays.android.MainActivity
import com.elinext.holidays.android.MyApplicationTheme
import com.elinext.holidays.android.utils.Constants
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.YearMonth

class MonthFragment : BaseFragment() {


    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val toast =
            if (granted) "Permission granted" else "You will not receive notifications about upcoming holidays"
        Toast.makeText(context, toast, Toast.LENGTH_SHORT)
    }
    private var restoredYear: Int? = null
        get() = if (field == 99) null else field
    private var restoredMonth: Int? = null
        get() = if (field == 99) null else field


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("system.out", "----->${this.javaClass.name}")
        restoredYear = arguments?.getInt(Constants.YEAR, 99)
        restoredMonth = arguments?.getInt(Constants.MONTH, 99)
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @Composable
    override fun GreetingView() {
        val yearMonth = if (restoredYear != null && restoredMonth != null) YearMonth.of(
            restoredYear!!, restoredMonth!!
        ) else YearMonth.now()
        MyApplicationTheme {
            val currentMonth = remember { yearMonth }
            val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
            val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
            val calendarState = rememberCalendarState(
                startMonth = startMonth,
                endMonth = endMonth,
                firstVisibleMonth = currentMonth,
                firstDayOfWeek = DayOfWeek.MONDAY
            )
            Scaffold(
                modifier = Modifier.fillMaxSize(), topBar = {
                    TopBar(getTitle(calendarState))
                }, backgroundColor = MaterialTheme.colors.background, contentColor = Color.White
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
        val allYearsState = viewModel.allHolidaysMapFlow.collectAsState(initial = null)
        DaysOfWeekTitle(daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY))
        if (allYearsState.value == null || allYearsMap.isEmpty()) {
            CircularProgressBar()
        } else {
            HorizontalCalendar(state = calendarState, dayContent = { Day(it) })
            HolidaysView(calendarState)
        }
    }
}