# Adoption & Child Care (Android)
Android app built with Jetpack Compose, Navigation, and Room. It supports adoption workflows and child-care management with a drawer layout, role-based dashboard cards, and local authentication with session persistence.

## Key Highlights
- Jetpack Compose UI with `ModalNavigationDrawer` and top-right Search.
- Room database (unified) for Users, Children, Families, Adoption Applications, Home Studies, Documents, Placements, Case Reports, Education, Medical, Finance.
- Login/Register wired to Room with SHA-256 password hashing; `SessionManager` persists session.
- Dashboard: square cards in two columns, showing live counts from Room; cards navigate to lists.
- Settings: profile (username/email/role) + app toggles (notifications, Wi‑Fi-only sync) persisted.

## Build & Run
- Android Studio ▶ or CLI: `./gradlew assembleDebug`
- Min SDK 24; target SDK 36.

## Structure (selected)
- `app/src/main/java/com/example/adoption_and_childcare/MainActivity.kt` — App shell, routes, drawer, search.
- `app/src/main/java/com/example/adoption_and_childcare/ui/compose/DashboardScreen.kt` — 2-column square cards + Room counts.
- `app/src/main/java/com/example/adoption_and_childcare/ui/compose/LoginScreen.kt` — Room-backed register/login + session.
- `app/src/main/java/com/example/adoption_and_childcare/ui/compose/SettingsScreen.kt` — Profile + preferences.
- `app/src/main/java/com/example/adoption_and_childcare/data/db/` — Room DB, DAOs, Entities.
- `backend/` — Node.js/Express backend server with MySQL support.

*A compassionate platform bridging adoptive families, children in need, and caregivers.*

## 📌 Key Features
- **Adoption Matching:** Create and browse detailed profiles for children and families. Use advanced filters to find the best matches and connect securely.
- **Child Records:** Securely track health, education, and legal documents for each child, accessible only to authorized users.
- **Caregiver Tools:** Set reminders, track developmental milestones, and access support resources tailored for caregivers.
- **Social Worker Dashboard:** Manage cases, monitor progress, and access agency/admin tools for streamlined workflows.
- **Community Support:** Participate in forums, chat with other adoptive families, and access a resource library for ongoing support.

## 🛠️ Technologies Used
- **Frontend (Android):** Jetpack Compose, Kotlin, Room Database
- **Backend:** Node.js, Express, MySQL
- **Database:** Room (local) + MySQL (backend)
- **Authentication:** SHA-256 password hashing with JWT tokens
- **Other Tools:**
  - Figma *(UI/UX design)*
  - GitHub *(version control & collaboration)*
  - GitHub Actions *(CI/CD for automated builds)*

## 🚀 Installation & Setup (For Developers)

1. **Clone the repo:**
   ```bash
   git clone https://github.com/Brianmwanza-bit/adoption-and-child-care-main.git
   ```
2. **Install backend dependencies:**
   ```bash
   cd backend
   npm install
   ```
3. **Configure environment variables:**
   - Copy `backend/.env.example` to `backend/.env`
   - Add your MySQL database credentials
4. **Run the backend server:**
   ```bash
   npm run dev
   ```
5. **Open the Android project in Android Studio:**
   - Open the project folder in Android Studio
   - Sync Gradle files
   - Run the app on an emulator or device

## 📸 Screenshots
| Home | Profile | Chat |
|------|---------|------|
| ![Home](https://screenshots/home.png) | ![Profile](https://screenshots/profile.png) | ![Chat](https://screenshots/chat.png) |

## 🌍 Target Audience
- Prospective adoptive parents
- Foster caregivers
- Social workers/NGOs
- Child welfare agencies

## 🤝 How to Contribute
1. Fork the project.
2. Create a branch:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add amazing feature"
   ```
4. Push to your branch:
   ```bash
   git push origin feature/your-feature
   ```
5. Open a Pull Request.

## 📜 License
MIT License. See [LICENSE](LICENSE) for details.

---

## Next Steps for You
- Add real screenshots in the `/screenshots` folder.
- Include a demo video link (if available).
- Update the license file as needed.

---

## 💡 Suggestions & Best Practices
- **Logo/Design:** Use Figma to design a simple, friendly logo (e.g., a heart, family, or home icon).
- **Android Development:**
  - [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI
  - [Room Database](https://developer.android.com/training/data-storage/room) for local data
  - [Navigation Component](https://developer.android.com/guide/navigation) for app navigation
- **UI/UX:** Prioritize accessibility, clear onboarding, and a supportive tone throughout the app.

---

*Feel free to reach out for more help with any section, design ideas, or technical setup!*

## Restoring the Database

To restore the database using the provided SQL file, follow these steps:

1. Make sure MySQL is running on your system.
2. Open a terminal or command prompt.
3. Run the following command (replace 'root' and database name if needed):

```
mysql -u root adoption_and_childcare_tracking_system_db < database/adoption_and_childcare_tracking_system_db.sql
```

- This will recreate the tables and data as exported.
- You can also use tools like DBeaver or phpMyAdmin to import the SQL file if you prefer a graphical interface.