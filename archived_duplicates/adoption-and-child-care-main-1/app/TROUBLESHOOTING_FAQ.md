# Troubleshooting & FAQ

This guide addresses common issues and frequently asked questions for the Adoption & Child Care app (backend and mobile).

---

## General

**Q: The app or backend won't start. What should I check?**
- Ensure all dependencies are installed (`npm install`, Gradle sync in Android Studio).
- Check for missing environment variables (`.env` file for backend).
- Review error messages in the terminal or logcat.

---

## Backend (Node.js/Express)

**Q: Database connection errors (ECONNREFUSED, access denied, etc.)?**
- Verify MySQL server is running and accessible.
- Check DB credentials in `.env`.
- Ensure the user has correct privileges.

**Q: Port already in use?**
- Stop any process using the port (default: 3000) or change the port in `.env`.

**Q: API docs not showing?**
- Make sure `/api-docs` route is set up and server is running.

---

## Mobile App (Android)

**Q: Gradle build fails or "JAVA_HOME not set"?**
- Install JDK 17+ and set `JAVA_HOME` environment variable.
- Restart Android Studio after changes.

**Q: App crashes on launch?**
- Check logcat for errors.
- Ensure `google-services.json` is present for Firebase features.
- Verify all permissions are granted.

**Q: Push notifications not received?**
- Confirm device is online and registered with Firebase.
- Check notification permission (Android 13+).
- Review Firebase Console for errors.

**Q: Cannot access camera or storage?**
- Ensure permissions are declared in `AndroidManifest.xml` and requested at runtime.
- Test on a real device (emulator may have limitations).

---

## Database

**Q: Schema changes not reflected?**
- Backend auto-migrates MySQL schema on startup. Restart backend after code changes.
- For Room (SQLite), increment the database version and provide a migration.

---

## Deployment

**Q: Docker build fails?**
- Check Dockerfile paths and base image.
- Ensure all files are present in build context.

**Q: App not working after deployment?**
- Check logs for errors.
- Verify environment variables and DB connectivity.

---

## Other

**Q: Where can I find more documentation?**
- See the onboarding guide, API reference, and other docs in the project.

**Q: Who do I contact for help?**
- Contact the project maintainer or open an issue in the repository.

---

**Still stuck?**
- Search error messages online or in the [official Android](https://developer.android.com/) and [Node.js](https://nodejs.org/en/docs/) docs. 