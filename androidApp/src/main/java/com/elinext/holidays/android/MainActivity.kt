package com.elinext.holidays.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elinext.holidays.di.Configuration
import com.elinext.holidays.di.EngineSDK
import com.elinext.holidays.di.PlatformType

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
}
