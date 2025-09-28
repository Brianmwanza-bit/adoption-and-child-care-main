package com.example.adoption_and_childcare

import android.app.Application

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

    }
}
// Initialize any libraries or configurations here if needed