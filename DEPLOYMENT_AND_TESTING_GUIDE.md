# 🚀 Deployment, Testing & CI/CD Guide

## 1. Building the Application
To build the Android application locally using Gradle:
```bash
./gradlew app:assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 2. Deploying to Connected Devices
To install and launch on connected Android emulators or physical USB devices:
```bash
# Check connected devices
adb devices

# Install APK on target device
adb -s <device_id> install -r app/build/outputs/apk/debug/app-debug.apk

# Launch MainActivity
adb -s <device_id> shell am start -n com.example.adoption_and_childcare/.MainActivity
```

---

## 3. Starting the Backend Server & Aiven Pipeline
To run the Node.js backend server connected to Aiven MySQL:
```bash
cd backend
npm install
npm run dev
```

---

## 4. GitHub Actions CI/CD Pipeline
The project includes a GitHub Actions workflow (`.github/workflows/android-build.yml`) that automatically:
1. Sets up Java 17 and Gradle environment.
2. Compiles `app:assembleDebug`.
3. Runs unit tests `app:testDebugUnitTest`.
4. Publishes release artifacts and builds directly to GitHub Releases upon push to `main`.
