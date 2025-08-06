# Store Listing Assets Checklist

Prepare the following assets and information for Play Store (and App Store, if cross-platform) submission:

## 1. App Icon
- 512x512 px PNG (already prepared: use c7.png)

## 2. Feature Graphic (Play Store)
- 1024x500 px PNG or JPEG
- Used as a banner at the top of your store listing

## 3. Screenshots
- At least 2-8 screenshots of the app in use
- Recommended sizes:
  - Phone: 1080x1920 px or similar
  - Tablet: 1920x1080 px or similar (optional)
- Show key features: login, dashboard, children, placements, notifications, analytics, map, etc.

## 4. App Description
- Short description (up to 80 characters)
- Full description (up to 4000 characters)
- Highlight main features, security, and benefits

## 5. Privacy Policy URL
- Link to your privacy policy (e.g., GitHub raw file or your website)

## 6. App Name
- As it should appear in the store (e.g., "Adoption & Child Care")

## 7. Category & Tags
- Choose the most relevant category (e.g., Social, Productivity, Parenting)
- Add relevant tags

## 8. Contact Details
- Email address for support
- (Optional) Website and phone number

## 9. Content Rating Questionnaire
- Complete the Play Console content rating questionnaire

## 10. Other (as required by store)
- Promo video (YouTube link, optional)
- Data safety form (required by Google)
- App version and release notes

---

**For more details, see the official [Play Store listing requirements](https://support.google.com/googleplay/android-developer/answer/1078870) and [App Store guidelines](https://developer.apple.com/app-store/review/guidelines/).** 

Great! Since you have the prerequisites (Android SDK, JDK, Gradle wrapper, and a code editor), you’re almost ready to build and run your project in Cursor.

### Here’s what you should do next to get your project running:

---

## 1. **Set the Android SDK Path**
- Open (or create) the file:  
  ```
  android/local.properties
  ```
- Add this line (replace `<your-username>` with your actual Windows username):
  ```
  sdk.dir=C:\\Users\\<your-username>\\AppData\\Local\\Android\\Sdk
  ```
- Save the file.

---

## 2. **Check JAVA_HOME**
- Make sure your JDK is installed (JDK 8 or higher).
- Set the `JAVA_HOME` environment variable to your JDK path (e.g., `C:\Program Files\Java\jdk-11.0.10`).

---

## 3. **Build the Project**
- Open a terminal in the `android` directory.
- Run:
  ```
  ./gradlew.bat assembleDebug
  ```
  or, to install on a device/emulator:
  ```
  ./gradlew.bat installDebug
  ```

---

## 4. **Run on Emulator or Device**
- Start an Android emulator from Android Studio, or connect your Android device with USB debugging enabled.
- The app should install and launch.

---

## 5. **Edit and Develop in Cursor**
- Use Cursor for code editing, refactoring, and version control.
- Use the terminal for building and running the app.

---

**If you encounter any errors or need help with any step (e.g., setting up `local.properties`, finding your SDK path, or running the build), just let me know and I’ll guide you through it!** 

Yes, your project is designed to be linked to a SQL database—specifically, a MySQL database.

### How the SQL Database is Linked:

- In your backend (`backend/server.js`), there is a MySQL connection setup using the `mysql2` Node.js package.
- The connection details (host, user, password, database, port) are specified in this section:
  ```js
  const db = mysql.createConnection({
    host: 'localhost', // or your remote host
    user: 'NUN',
    password: 'NUN',
    database: 'adoption_and_childcare_tracking_system_db',
    port: 3306,
    multipleStatements: true
  });
  ```
- This means your backend expects to connect to a MySQL database named `adoption_and_childcare_tracking_system_db` using the username and password you provided.

### What This Means:
- **If you have MySQL (or MariaDB) running on your machine (or a remote server) and the database exists, your backend will connect to it and use it for all data storage.**
- **phpMyAdmin** is just a web interface for managing your MySQL database. The actual connection is to the MySQL server, not phpMyAdmin itself.

### How to Check if the Database is Linked and Working:
1. Make sure MySQL is running and accessible at the host/port you specified.
2. Make sure the database `adoption_and_childcare_tracking_system_db` exists.
3. Make sure the user `NUN` with password `NUN` has access to that database.
4. When you start your backend server, if there are no connection errors, the backend is successfully linked to the SQL database.

---

**If you want to test the connection or see if the backend can access the database, just let me know and I can guide you through a quick test!** 