package com.elinext.holidays.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.elinext.holidays.di.Configuration
import com.elinext.holidays.di.EngineSDK
import com.elinext.holidays.di.PlatformType
import com.elinext.holidays.models.Holiday

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EngineSDK.init(
            configuration = Configuration(
                platformType = PlatformType.Android("1.0", "1")
            )
        )
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
