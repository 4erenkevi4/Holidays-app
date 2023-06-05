package com.elinext.holidays.android

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.elinext.holidays.di.Configuration
import com.elinext.holidays.di.EngineSDK
import com.elinext.holidays.di.PlatformType
import com.elinext.holidays.models.Holiday
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    var allYearsMap: MutableMap<Int, List<Holiday>?> = mutableMapOf()
        private set
    private val viewModel: HolidaysViewModel by viewModels()
    var listUpcomingHolidays = listOf<Holiday>()
        private set


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EngineSDK.init(
            configuration = Configuration(
                platformType = PlatformType.Android("1.0", "1")
            )
        )
        if (allYearsMap.isEmpty()) {
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
        viewModel.upcomingHolidaysLivedata.observe(this) {
            listUpcomingHolidays = it
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
