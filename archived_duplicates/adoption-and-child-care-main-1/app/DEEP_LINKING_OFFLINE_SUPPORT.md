# Deep Linking & Offline Support Guide

This guide covers how to add deep linking and robust offline/poor connectivity support to your Android app.

---

## 1. Deep Linking

### a. Configure Intent Filters in AndroidManifest.xml
Add an intent filter to your main activity to handle app links:
```xml
<activity android:name=".MainActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="https" android:host="yourdomain.com" />
    </intent-filter>
</activity>
```
- Replace `yourdomain.com` with your actual domain or use a custom scheme (e.g., `adoptionapp://`).

### b. Handle Deep Links in MainActivity
```kotlin
override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    intent?.data?.let { uri ->
        // Parse URI and navigate to the appropriate screen
        // Example: adoptionapp://child/123
        if (uri.pathSegments.firstOrNull() == "child") {
            val childId = uri.lastPathSegment
            // Navigate to child details screen
        }
    }
}
```
- Integrate with your navigation logic (NavController or manual navigation).

---

## 2. Offline/Poor Connectivity Support

### a. Use Room for Local Persistence
- All critical data (children, documents, tasks, etc.) should be stored in Room database.
- UI should observe Room data (LiveData/StateFlow) for instant updates.

### b. Sync with Remote Backend Using WorkManager
- Use `WorkManager` to schedule background syncs (upload/download changes when online).
- Example:
```kotlin
val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
WorkManager.getInstance(context).enqueue(syncRequest)
```
- Implement `SyncWorker` to handle API calls and merge data.

### c. Detect Connectivity
- Use `ConnectivityManager` or libraries like [AndroidX Connectivity](https://developer.android.com/reference/android/net/ConnectivityManager) to detect network status.
- Show a Snackbar or banner when offline.

### d. Handle API Failures Gracefully
- Catch network errors and show user-friendly messages.
- Queue failed operations for retry when back online.

---

**For more details:**
- [Android Deep Links](https://developer.android.com/training/app-links/deep-linking)
- [Room Persistence](https://developer.android.com/training/data-storage/room)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Offline Support Patterns](https://developer.android.com/topic/performance/vitals/offline) 