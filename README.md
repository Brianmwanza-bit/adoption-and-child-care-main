# Adoption & Child Care (Android)

A comprehensive Android application built with **Jetpack Compose**, **Hilt**, and **Room**, designed to streamline adoption workflows and child-care management. This platform bridges the gap between adoptive families, caregivers, and social workers with a modern, offline-first approach.

## 🚀 Key Highlights
- **Modern UI:** Built entirely with Jetpack Compose, featuring a `ModalNavigationDrawer` and role-based dashboard cards.
- **Offline-First Architecture:** Integrated Room database with a **Sync Queue** system for seamless data management in low-connectivity environments.
- **Unified Data Model:** Manages Users, Children, Families, Adoption Applications, Home Studies, Documents, Placements, Case Reports, Medical Records, and Finance.
- **Secure Authentication:** Local and remote authentication with SHA-256 password hashing and session persistence via `EncryptedPreferences`.
- **Background Sync:** Uses **WorkManager** to handle data synchronization with a remote API in the background.

## 🛠️ Technologies Used
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Dependency Injection:** Hilt (Dagger)
- **Database:** Room Persistence Library
- **Networking:** Retrofit + OkHttp
- **Background Tasks:** WorkManager
- **Image Loading:** Coil
- **Security:** AndroidX Security (EncryptedSharedPreferences)

## 🏗️ Project Structure
- `app/src/main/java/com/example/adoption_and_childcare/`
    - `data/` — Room Entities, DAOs, and Repositories (BaseSyncRepository logic).
    - `ui/compose/` — Jetpack Compose screens (Dashboard, Families, Medical, etc.).
    - `viewmodel/` — Hilt-injected ViewModels managing UI state.
    - `network/` — API Service definitions and Constants.
    - `service/` — Background services (SOS Location, Firebase Messaging).

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17
- Min SDK: 24 (Android 7.0)
- Target SDK: 35 (Android 15)

### Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-repo/Adoption-And-Childcare.git
   ```
2. **Open in Android Studio:**
   Select the project root folder and wait for Gradle sync to complete.
3. **Configure API:**
   Update the `BASE_URL` in `gradle.properties` or your build configuration to point to your backend server.
4. **Build & Run:**
   Click the **Run** button in Android Studio or use the CLI:
   ```bash
   ./gradlew assembleDebug
   ```

## 📊 Feature Modules
- **Families Management:** Complete CRUD for prospective families, including detailed contact and license tracking.
- **Child 360° Profile:** Comprehensive view of a child's history, medical records, and education.
- **Adoption Workflow:** Track applications from initial home study to final placement.
- **Finance Tracking:** Manage allowances, expenses, and transaction history.
- **Case Reports:** Real-time reporting for social workers with PDF generation support.

## 🤝 Contributing
1. Fork the project.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

## 📜 License
MIT License. See [LICENSE](LICENSE) for details.

---

## 🗄️ Database Restoration
To restore the database using a provided SQL file (for backend integration/testing):
```bash
mysql -u root adoption_and_childcare_db < database/adoption_and_childcare_db.sql
```

*Built with ❤️ to support children and the families who care for them.*
