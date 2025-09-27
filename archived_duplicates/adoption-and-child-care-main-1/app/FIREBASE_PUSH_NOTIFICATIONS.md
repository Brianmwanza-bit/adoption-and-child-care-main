# Firebase Cloud Messaging (FCM) Push Notifications Integration

Follow these steps to add push notifications to your Android app using Firebase Cloud Messaging (FCM):

---

## 1. Create a Firebase Project
- Go to [Firebase Console](https://console.firebase.google.com/)
- Click "Add project" and follow the prompts
- Add your Android app (register package name, e.g., `com.adoptionapp`)
- Download `google-services.json` and place it in `app/` directory

## 2. Add Gradle Dependencies
- In `project/build.gradle`:
  ```gradle
  classpath 'com.google.gms:google-services:4.3.15' // or latest
  ```
- In `app/build.gradle`:
  ```gradle
  implementation 'com.google.firebase:firebase-messaging:23.4.0' // or latest
  apply plugin: 'com.google.gms.google-services'
  ```

## 3. Update AndroidManifest.xml
- Add the FCM service:
  ```xml
  <service
      android:name=".MyFirebaseMessagingService"
      android:exported="false">
      <intent-filter>
          <action android:name="com.google.firebase.MESSAGING_EVENT" />
      </intent-filter>
  </service>
  ```
- Add the notification permission (Android 13+):
  ```xml
  <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
  ```

## 4. Implement the Messaging Service
- Create `MyFirebaseMessagingService.kt`:
  ```kotlin
  import com.google.firebase.messaging.FirebaseMessagingService
  import com.google.firebase.messaging.RemoteMessage
  import android.util.Log
  import androidx.core.app.NotificationCompat
  import androidx.core.app.NotificationManagerCompat
  import android.app.NotificationChannel
  import android.app.NotificationManager
  import android.os.Build
  import android.content.Context

  class MyFirebaseMessagingService : FirebaseMessagingService() {
      override fun onMessageReceived(remoteMessage: RemoteMessage) {
          // Handle FCM messages here.
          Log.d("FCM", "From: ${remoteMessage.from}")
          remoteMessage.notification?.let {
              showNotification(it.title, it.body)
          }
      }

      private fun showNotification(title: String?, message: String?) {
          val channelId = "default_channel"
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              val channel = NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
              val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
              manager.createNotificationChannel(channel)
          }
          val builder = NotificationCompat.Builder(this, channelId)
              .setSmallIcon(R.drawable.ic_notification)
              .setContentTitle(title ?: "Notification")
              .setContentText(message ?: "")
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
          with(NotificationManagerCompat.from(this)) {
              notify(System.currentTimeMillis().toInt(), builder.build())
          }
      }
  }
  ```
- Replace `R.drawable.ic_notification` with your app's notification icon.

## 5. Request Notification Permission (Android 13+)
- In your main activity:
  ```kotlin
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
  }
  ```

## 6. Test Push Notifications
- Use Firebase Console to send a test notification to your device.

---

**For more details, see the [official FCM documentation](https://firebase.google.com/docs/cloud-messaging/android/client).** 