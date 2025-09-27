# Developer Onboarding Guide

Welcome to the Adoption & Child Care project! This guide will help you get started as a developer.

---

## 1. Prerequisites
- Node.js (v16+ recommended)
- MySQL server (remote or local)
- Android Studio (for mobile app)
- JDK 17+ (Eclipse Temurin recommended)
- Git

---

## 2. Backend Setup
1. `cd backend/`
2. Install dependencies:
   ```sh
   npm install
   ```
3. Configure environment variables:
   - Copy `.env.example` to `.env` and set DB credentials, JWT secret, etc.
4. Start the backend server:
   ```sh
   node server.js
   ```
5. Access API docs at [http://localhost:3000/api-docs](http://localhost:3000/api-docs)

---

## 3. Mobile App Setup
1. Open the project in Android Studio.
2. Ensure `google-services.json` is in the `app/` directory (for Firebase features).
3. Connect a device or start an emulator.
4. Build and run the app using the green "Run" button or:
   ```sh
   ./gradlew assembleDebug
   ```

---

## 4. Database
- MySQL schema is auto-migrated by backend on startup.
- Room (SQLite) schema is managed by Room in the Android app.

---

## 5. Testing
- Backend: Run tests with `npm test` (Jest/Supertest)
- Mobile: Use Android Studio test runner or `./gradlew test`

---

## 6. Project Structure
- `backend/` - Node.js/Express backend
- `app/` or `android/` - Android mobile app (Kotlin/Jetpack Compose)
- `res/` - Shared resources (icons, images)

---

## 7. Useful Docs
- [API Reference](./SWAGGER_API_REFERENCE.md)
- [Permissions Checklist](../app/PERMISSIONS_CHECKLIST.md)
- [Push Notifications](../app/FIREBASE_PUSH_NOTIFICATIONS.md)
- [Crashlytics & Analytics](../app/FIREBASE_CRASHLYTICS_ANALYTICS.md)
- [Deep Linking & Offline](../app/DEEP_LINKING_OFFLINE_SUPPORT.md)

---

For any issues, check the README files or contact the project maintainer. 