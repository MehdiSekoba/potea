package com.mehdisekoba.potea.utils

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //Calligraphy
        ViewPump.init(
                ViewPump.builder().addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/urbanist_medium.ttf")
                            .build()
                    )
                ).build()
        )
    }
}