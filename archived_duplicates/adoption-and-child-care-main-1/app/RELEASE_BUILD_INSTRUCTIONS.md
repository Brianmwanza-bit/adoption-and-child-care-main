# Android Release Build Instructions

Follow these steps to generate a signed APK or AAB for Play Store submission.

## 1. Generate a Keystore (if you don't have one)

```
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```
- Remember the password and alias you set here.

## 2. Configure `gradle.properties`

Add the following (replace with your values):
```
MYAPP_RELEASE_STORE_FILE=my-release-key.jks
MYAPP_RELEASE_KEY_ALIAS=my-key-alias
MYAPP_RELEASE_STORE_PASSWORD=your-keystore-password
MYAPP_RELEASE_KEY_PASSWORD=your-key-password
```

## 3. Update `build.gradle`

In your `app/build.gradle`, add:
```
signingConfigs {
    release {
        storeFile file(MYAPP_RELEASE_STORE_FILE)
        storePassword MYAPP_RELEASE_STORE_PASSWORD
        keyAlias MYAPP_RELEASE_KEY_ALIAS
        keyPassword MYAPP_RELEASE_KEY_PASSWORD
    }
}
buildTypes {
    release {
        signingConfig signingConfigs.release
        minifyEnabled false
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

## 4. Build the APK or AAB

- For APK:
  ```
  ./gradlew assembleRelease
  ```
- For AAB (recommended for Play Store):
  ```
  ./gradlew bundleRelease
  ```
- The output will be in `app/build/outputs/apk/release/` or `app/build/outputs/bundle/release/`

## 5. Test the Release Build
- Install the APK on a real device:
  ```
  adb install app-release.apk
  ```
- Make sure everything works as expected.

## 6. Upload to Play Console
- Go to https://play.google.com/console
- Create a new app or select your app.
- Upload the signed APK or AAB.
- Fill in the required listing details (screenshots, description, privacy policy, etc.).
- Submit for review.

---

**For more details, see the official [Play Console documentation](https://developer.android.com/studio/publish).** 