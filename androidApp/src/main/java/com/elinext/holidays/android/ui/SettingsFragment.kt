package com.elinext.holidays.android.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.elinext.holidays.android.MyApplicationTheme
import com.elinext.holidays.android.MyNotificationReceiver
import com.elinext.holidays.android.Notification
import com.elinext.holidays.android.R
import com.elinext.holidays.models.Holiday
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

class SettingsFragment : BaseFragment() {

    private val listUpcomingNotifications = mutableListOf<Notification>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUpcomingNotifications()
        lifecycleScope.launch {
            viewModel.initListOfCountries()
        }
        view.findViewById<ComposeView>(R.id.compose_view).setContent {
            GreetingView()
        }
        viewModel.upcomingHolidaysLivedata.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                setNotificationsForDates(it)
            }
        }
        Log.d("system.out", "----->${this.javaClass.name}")
    }

    private fun initUpcomingNotifications() {
        val context = context ?: return
        for (i in YearMonth.now().month.value..12) {
            val notification = viewModel.getNotificationInPreferences(context = context, i)
            notification?.let {
                listUpcomingNotifications.add(it)
            }
        }
    }

    @Composable
    override fun GreetingView() {
        val context = context ?: return
        val isChecked = viewModel.getNotificationFromSp(context)
        val checkedState = remember { mutableStateOf(isChecked) }
        val setNotificationState = remember { mutableStateOf(listUpcomingNotifications.isEmpty()) }
        val time = remember { mutableStateOf(12) }
        val date = remember { mutableStateOf(22) }
        val datePickerDialog = DatePickerDialog(
            context
        ).apply {
            val calendarTime = Calendar.getInstance()
            calendarTime.set(Calendar.MONTH, calendarTime.get(Calendar.MONTH) + 1)
            calendarTime.set(Calendar.DATE, 1)
            datePicker.minDate = calendarTime.timeInMillis
            calendarTime.set(Calendar.DATE, calendarTime.getMaximum(Calendar.DATE))
            datePicker.maxDate = calendarTime.timeInMillis
            this.setOnDateSetListener { _, _, _, dayOfMonth ->
                date.value = dayOfMonth
            }
        }
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hour: Int, _: Int ->
                time.value = hour
            }, 12, 0, true
        )
        MyApplicationTheme {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    TopBar()
                },
                backgroundColor = Color.White,
                contentColor = Color.White
            ) { value ->
                val padding = value
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color.White)
                    ) {
                        // This is Notification Row
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Image(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .align(Alignment.CenterStart),
                                painter = painterResource(id = R.drawable.baseline_notifications_none_24),
                                contentDescription = null,

                                )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    modifier = Modifier
                                        .padding(start = 64.dp),
                                    text = "Notifications",
                                    color = MaterialTheme.colors.onSurface
                                )
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                                Switch(
                                    checked = checkedState.value,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colors.primaryVariant,
                                        uncheckedThumbColor = MaterialTheme.colors.onSurface
                                    ),
                                    onCheckedChange = {
                                        viewModel.saveNotificationToSp(context, it)
                                        checkedState.value = it
                                    })
                            }
                        }
                        if (checkedState.value) {

                            Box(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                                Image(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 6.dp),
                                    painter = painterResource(id = R.drawable.baseline_location_on_24),
                                    contentDescription = null,

                                    )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 64.dp),
                                        text = "Office country",
                                        color = MaterialTheme.colors.onSurface
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    )
                                    DropDownMenu()
                                }
                            }
                            CreateSettingRow(
                                "Day of notification",
                                R.drawable.baseline_calendar_month_24,
                                date.value.toString()
                            ) { datePickerDialog.show() }
                            CreateSettingRow(
                                "Time of notification",
                                R.drawable.baseline_access_time_24,
                                time.value.toString()
                            ) { timePickerDialog.show() }
                            Spacer(modifier = Modifier.padding(16.dp))

                            if (setNotificationState.value) {
                                Button(modifier = Modifier.padding(30.dp), onClick = {
                                    setNotificationState.value = true
                                    viewModel.saveNotificationDateToSp(context, date.value)
                                    viewModel.saveNotificationHourToSp(context, time.value)
                                    lifecycleScope.launch {
                                        viewModel.getHolidays(
                                            context,
                                            Calendar.getInstance().get(Calendar.YEAR)
                                        )
                                    }
                                    Toast.makeText(
                                        context,
                                        "Notifications are set!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }, shape = RoundedCornerShape(20)) {
                                    Text(text = "Set notifications")
                                }
                            }
                            else {
                                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.background(
                                    MaterialTheme.colors.background
                                ).padding(horizontal = 16.dp)) {
                                    Text(
                                        text = "Upcoming push notifications:",
                                        style = MaterialTheme.typography.subtitle1,
                                        color = MaterialTheme.colors.primary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        textAlign = TextAlign.Start
                                    )
                                    NotificationList()
                                }
                            }

                        }
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun NotificationList() {
        val context = context?: return
        val updatedList = remember { mutableStateListOf(*listUpcomingNotifications.toTypedArray()) }

        SwipeRefresh(state = rememberSwipeRefreshState(false), onRefresh = { /* Handle refresh */ }) {
            LazyColumn {
                items(updatedList, key = { notification -> notification.id }) { notification ->
                    val dismissState = rememberDismissState()

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                        dismissThresholds = { direction ->
                            FixedThreshold(56.dp)
                        },
                        background = { /* Background content */ },
                        dismissContent = {
                            NotificationItem(notification)
                        }
                    )

                    LaunchedEffect(dismissState.isDismissed(DismissDirection.EndToStart)) {
                        if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                            updatedList.remove(notification)
                            viewModel.removeNotificationFromSP(context, notification.month)
                            listUpcomingNotifications.remove(notification)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun NotificationItem(notification: Notification) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = 4.dp,
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_notifications_none_24),
                    contentDescription = "Notification Icon",
                    tint = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "${notification.day}.${notification.month}.${YearMonth.now().year}",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.primary
                    )
                    Text(
                        text = notification.country,
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.primary
                    )
                    Text(
                        text = notification.description,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }



    @Composable
    fun TopBar() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        activity?.onBackPressed()
                    },
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                tint = Color.Gray,
                contentDescription = "back"
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .width(200.dp),
                text = "Settings",
                fontSize = 20.sp,
                color = MaterialTheme.colors.onSurface
            )
        }
    }


    @Composable
    fun createPickRow(initialValue: String, action: () -> Unit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = Modifier.padding(horizontal = 16.dp), text = initialValue)
            Image(painter = painterResource(id = R.drawable.ic_tryangle), contentDescription = null)
        }
    }

    @Composable
    fun CreateSettingRow(
        title: String,
        iconResId: Int,
        initialPickerValue: String,
        pickerAction: () -> Unit
    ) {
        Box(modifier = Modifier
            .padding(top = 30.dp, start = 16.dp, end = 16.dp)
            .clickable { pickerAction.invoke() }) {
            Image(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .align(Alignment.CenterStart),
                painter = painterResource(id = iconResId),
                contentDescription = null,

                )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .padding(start = 64.dp),
                    text = title,
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                createPickRow(initialPickerValue, pickerAction)
            }
        }
    }

    private fun setNotificationsForDates(dates: List<Holiday>) {
        val context = context ?: return
        val calendar = Calendar.getInstance()
        val day = viewModel.getNotificationDateFromSp(context)
        val hour = viewModel.getNotificationHourFromSp(context)
        val country = viewModel.getOfficeIdInPreferences(context, true)
        for (i in YearMonth.now().month.value..12) {
            val holidays = dates.filter { it.getMonth() == i }
            val desc = makeDescription(
                i,
                holidays.firstOrNull()?.getYear() ?: YearMonth.now().year,
                holidays.firstOrNull()?.getDay(),
                holidays
            )
            val title = "${getMonthByNumber(i)} holidays report in $country"
            val notifyId = System.currentTimeMillis().toInt()
            calendar.set(Calendar.MONTH, i)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, 0)
            startAlarm(calendar, notifyId, title, desc)
            viewModel.saveNotificationToPreferences(context, country, desc, i, day)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonthByNumber(monthnum: Int): String {
        val c = Calendar.getInstance(Locale.ENGLISH)
        val month = SimpleDateFormat("MMMM", Locale.ENGLISH)
        c[Calendar.MONTH] = monthnum - 1
        return month.format(c.time)
    }


    private fun makeDescription(month: Int, year: Int, day: Int?, holidays: List<Holiday>): String {
        val days = viewModel.getWorkingDaysOfMonth(YearMonth.now().year, month)
        val mMonth = getMonthByNumber(month)
        val title = "In $mMonth $year, $days working days\n"
        val desc = if (holidays.isEmpty()) ""
        else {
            if (holidays.size == 1)
                "and 1 additional holiday: \n${holidays.first().comment}, $day $month"
            else {
                if (holidays.size == 2)
                    "and 2 additional holidays: \n ${holidays.first().comment}, $day $month \n ${holidays.last().comment}, $day $month"
                else {
                    "and ${holidays.size} additional holidays"
                }
            }
        }
        val finalDesc = java.lang.StringBuilder().append(title).append(desc)
        return finalDesc.toString()
    }


    fun startAlarm(
        calendar: Calendar,
        notifyId: Int,
        titleOfNotification: String,
        editTextDesc: String
    ) {
        val context = context ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyNotificationReceiver::class.java)
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        intent.putExtra("Title", titleOfNotification)
        intent.putExtra("Description", editTextDesc)
        intent.putExtra("Month", calendar.get(Calendar.MONTH))
        intent.putExtra("id", notifyId)

        val pendingFlags: Int =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(context, notifyId, intent, pendingFlags)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

    }
}