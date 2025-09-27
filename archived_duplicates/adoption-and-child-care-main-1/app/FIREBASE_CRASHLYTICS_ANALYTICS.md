# Firebase Crashlytics & Analytics Integration

Follow these steps to add crash reporting and analytics to your Android app using Firebase Crashlytics and Analytics:

---

## 1. Add Firebase to Your Project
- If not already done, follow the steps in the FCM guide to create a Firebase project and add your app.
- Ensure `google-services.json` is in your `app/` directory.

## 2. Add Gradle Dependencies
- In `project/build.gradle`:
  ```gradle
  classpath 'com.google.gms:google-services:4.3.15' // or latest
  ```
- In `app/build.gradle`:
  ```gradle
  implementation 'com.google.firebase:firebase-crashlytics:18.6.1' // or latest
  implementation 'com.google.firebase:firebase-analytics:21.6.1' // or latest
  apply plugin: 'com.google.gms.google-services'
  apply plugin: 'com.google.firebase.crashlytics'
  ```

## 3. Initialize Firebase in Your App
- In your `Application` class (or `MainActivity` if you don't have one):
  ```kotlin
  import com.google.firebase.analytics.FirebaseAnalytics
  import com.google.firebase.crashlytics.FirebaseCrashlytics
  // ...
  class MyApp : Application() {
      override fun onCreate() {
          super.onCreate()
          // Initialize Analytics
          val analytics = FirebaseAnalytics.getInstance(this)
          // Enable Crashlytics collection
          FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
      }
  }
  ```
- Register your `Application` class in `AndroidManifest.xml`:
  ```xml
  <application
      android:name=".MyApp"
      ... >
      ...
  </application>
  ```

## 4. Using Analytics
- Log custom events:
  ```kotlin
  val analytics = FirebaseAnalytics.getInstance(context)
  val bundle = Bundle().apply {
      putString(FirebaseAnalytics.Param.ITEM_ID, "id123")
      putString(FirebaseAnalytics.Param.ITEM_NAME, "ChildProfile")
      putString(FirebaseAnalytics.Param.CONTENT_TYPE, "profile")
  }
  analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
  ```

## 5. Using Crashlytics
- Log non-fatal errors:
  ```kotlin
  FirebaseCrashlytics.getInstance().log("Non-fatal error occurred")
  FirebaseCrashlytics.getInstance().recordException(Exception("Sample exception"))
  ```
- Force a test crash:
  ```kotlin
  FirebaseCrashlytics.getInstance().sendUnsentReports()
  throw RuntimeException("Test Crash")
  ```

## 6. Build and Run
- Build and run your app on a real device or emulator.
- Check the Firebase Console for analytics events and crash reports.

---

**For more details, see the [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics/get-started?platform=android) and [Firebase Analytics](https://firebase.google.com/docs/analytics/get-started?platform=android) documentation.** 