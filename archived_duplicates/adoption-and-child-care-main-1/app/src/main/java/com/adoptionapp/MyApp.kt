package com.adoptionapp

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Analytics
        FirebaseAnalytics.getInstance(this)
        // Enable Crashlytics collection
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}