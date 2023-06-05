package com.elinext.holidays.android

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.elinext.holidays.di.Configuration
import com.elinext.holidays.di.EngineSDK
import com.elinext.holidays.di.PlatformType
import com.elinext.holidays.models.Holiday
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    var allYearsMap: MutableMap<Int, List<Holiday>?> = mutableMapOf()
    val viewModel: HolidaysViewModel by viewModels()
    var listUpcomingNotifications = listOf<Notification>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EngineSDK.init(
            configuration = Configuration(
                platformType = PlatformType.Android("1.0", "1")
            )
        )
        if (allYearsMap.isEmpty()){
            lifecycleScope.launch {
                viewModel.getHolidays(
                    this@MainActivity,
                    Calendar.getInstance().get(Calendar.YEAR)
                )
            }
        }
        lifecycleScope.launch {
            viewModel.allHolidaysMapFlow.collect() {
                allYearsMap = it
            }
        }
        setContentView(R.layout.activity_main)
    }


    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
