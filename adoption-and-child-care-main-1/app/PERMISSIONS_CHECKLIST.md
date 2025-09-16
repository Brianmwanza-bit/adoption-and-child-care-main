# Android Permissions Checklist

Review and declare the following permissions in your app as needed:

## 1. Internet
- Required for all network operations (API calls, sync)
- Add to `AndroidManifest.xml`:
  ```xml
  <uses-permission android:name="android.permission.INTERNET" />
  ```

## 2. Camera
- Required for taking photos (child/document photo upload)
- Add to `AndroidManifest.xml`:
  ```xml
  <uses-permission android:name="android.permission.CAMERA" />
  ```
- Request at runtime before using camera

## 3. Storage (Read/Write)
- Required for selecting/uploading files/photos
- Add to `AndroidManifest.xml`:
  ```xml
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  ```
- Request at runtime (Android 6.0+)

## 4. Location
- Required for map features (showing user/family locations)
- Add to `AndroidManifest.xml`:
  ```xml
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
  ```
- Request at runtime before accessing location

## 5. Notifications
- Required for push notifications (Android 13+)
- Add to `AndroidManifest.xml`:
  ```xml
  <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
  ```
- Request at runtime (Android 13+)

## 6. Other (if needed)
- Contacts, phone, etc. (not required for current features)

---

## How to Handle Permissions
- Always check and request permissions at runtime before accessing sensitive features (camera, storage, location, notifications).
- Handle permission denial gracefully (show message, disable feature, etc.).
- Test on multiple Android versions (6.0+ for runtime permissions).

**For more details, see the official [Android permissions guide](https://developer.android.com/guide/topics/permissions/overview).** 