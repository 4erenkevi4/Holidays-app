package com.elinext.holidays.android.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.elinext.holidays.android.HolidaysViewModel
import com.elinext.holidays.android.MyApplicationTheme
import com.elinext.holidays.android.MyNotificationReceiver
import com.elinext.holidays.android.R
import com.kizitonwose.calendar.compose.rememberCalendarState
import java.time.DayOfWeek
import java.util.*

class SettingsFragment : Fragment() {

    val viewModel: HolidaysViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<ComposeView>(R.id.compose_view).setContent {
            GreetingView()
        }
    }

    @Composable
    fun GreetingView() {
        MyApplicationTheme {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    TopBar()
                },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.White
            ) { value ->
                val padding = value
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 16.dp)) {
                        NotificationRow()
                        CreateSettingRow("Office country", R.drawable.ic_geo)
                        CreateSettingRow("Day of notification", R.drawable.ic_calendar_date)
                        CreateSettingRow("Time of notification", R.drawable.ic_notification_time)

                    }
                }
            }
        }
    }

    @Composable
    fun TopBar() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        //onBackPressed
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
    fun NotificationRow(
    ) {
        val context = context ?: return
        val isChecked = viewModel.getNotificationFromSp(context)

        val checkedState = remember { mutableStateOf(isChecked) }
        Box() {
            Image(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .align(Alignment.CenterStart),
                painter = painterResource(id = R.drawable.ic_notification),
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
                        checkedState.value = it
                    })
            }
        }
    }

    @Composable
    fun CreateSettingRow(
        title: String,
        iconResId: Int,
    ) {
        val context = context ?: return
        val isChecked = viewModel.getNotificationFromSp(context)

        val checkedState = remember { mutableStateOf(isChecked) }
        Box() {
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
                Switch(
                    checked = checkedState.value,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colors.primaryVariant,
                        uncheckedThumbColor = MaterialTheme.colors.onSurface
                    ),
                    onCheckedChange = {
                        checkedState.value = it
                    })
            }
        }
    }

    fun setNotificationsForDates(context: Context, dates: List<Date>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Перебираем каждую дату в списке и создаем отложенное уведомление для каждой даты
        for (date in dates) {
            // Создаем PendingIntent для уведомления
            val notificationIntent = Intent(context, MyNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            // Создаем календарь для выбранной даты и устанавливаем время уведомления
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, 9) // Устанавливаем часы
            calendar.set(Calendar.MINUTE, 0) // Устанавливаем минуты

            // Устанавливаем отложенное уведомление
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

}