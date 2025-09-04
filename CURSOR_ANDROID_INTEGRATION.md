# Cursor + Android Studio Integration Guide

This guide will help you set up seamless integration between Cursor and Android Studio for the Adoption & Child Care project.

## Quick Setup

### 1. Run Integration Script
```bash
# PowerShell (Recommended)
.\android-studio-integration.ps1

# Or Command Prompt
android-studio-integration.bat
```

### 2. Install Required Extensions in Cursor
- Kotlin Language (fwcd.kotlin)
- Extension Pack for Java (vscjava.vscode-java-pack)
- Gradle for Java (vscjava.vscode-gradle)
- Android iOS Emulator (DiemasMichiels.emulate)

## Project Structure

```
adoption-and-child-care-main/
├── .vscode/                 # Cursor configuration
│   ├── settings.json       # Project settings
│   ├── launch.json         # Debug configurations
│   ├── tasks.json          # Build tasks
│   └── extensions.json     # Recommended extensions
├── android/                # Android Studio project
│   ├── .idea/             # Android Studio configuration
│   ├── app/               # Android app module
│   └── build.gradle       # Gradle configuration
├── backend/               # Node.js backend
├── src/                   # Web frontend
└── capacitor.config.json  # Capacitor configuration
```

## Development Workflow

### Using Cursor for Web Development
1. Open the project root in Cursor
2. Edit web files in `src/` directory
3. Use integrated terminal for npm commands
4. Use tasks (Ctrl+Shift+P > Tasks: Run Task) for common operations

### Using Android Studio for Native Development
1. Open Android Studio
2. Import the `android` folder as a project
3. Edit Kotlin/Java files in `app/src/main/kotlin/`
4. Use Android Studio's built-in tools for:
   - Layout design
   - Debugging
   - APK building
   - Device testing

### Synchronizing Changes
```bash
# After making web changes, sync with Android
npx cap sync android

# Open Android Studio from Cursor
npx cap open android
```

## Available Tasks in Cursor

Access via `Ctrl+Shift+P` > `Tasks: Run Task`:

- **Android: Clean Build** - Clean the Android project
- **Android: Build Debug APK** - Build debug APK
- **Android: Build Release APK** - Build release APK
- **Android: Install Debug APK** - Install debug APK on device
- **Android: Run Tests** - Run Android tests
- **Backend: Start Server** - Start Node.js backend
- **Backend: Install Dependencies** - Install npm packages
- **Capacitor: Sync** - Sync web changes to Android
- **Capacitor: Open Android Studio** - Open Android Studio

## Debug Configurations

Available via `F5` or `Run and Debug` panel:

- **Debug Android App** - Debug the Android application
- **Debug Backend Server** - Debug the Node.js backend
- **Debug Backend Node.js** - Debug with Node.js

## Key Features

### 1. IntelliSense Support
- Full Kotlin/Java IntelliSense in Cursor
- Android SDK autocomplete
- Gradle script support

### 2. Integrated Terminal
- PowerShell with proper Android SDK paths
- Gradle wrapper commands
- npm/node commands

### 3. File Associations
- `.kt` files open with Kotlin syntax highlighting
- `.gradle` files with Gradle support
- Android XML files with proper formatting

### 4. Build Integration
- One-click build tasks
- Error highlighting and problem matcher
- Integrated output panels

## Troubleshooting

### Android SDK Not Found
1. Install Android Studio
2. Open Android Studio > SDK Manager
3. Install Android SDK
4. Update `android/local.properties` with correct path

### Gradle Build Fails
1. Check Java version (JDK 8+ required)
2. Run `android\gradlew.bat --version`
3. Clean project: `gradlew clean`

### Capacitor Sync Issues
1. Ensure web build is complete
2. Run `npx cap sync android`
3. Check `capacitor.config.json` configuration

### Cursor Extensions Not Working
1. Install recommended extensions
2. Reload Cursor window (Ctrl+Shift+P > Reload Window)
3. Check Java extension settings

## Best Practices

1. **Use Cursor for**: Web development, project management, file editing
2. **Use Android Studio for**: Native Android development, debugging, layout design
3. **Sync regularly**: Run `npx cap sync android` after web changes
4. **Version control**: Both IDEs work with the same Git repository
5. **Debugging**: Use Cursor for web debugging, Android Studio for native debugging

## Quick Commands

```bash
# Start development server
npm start

# Build and sync to Android
npm run build
npx cap sync android

# Open Android Studio
npx cap open android

# Build Android APK
cd android
gradlew assembleDebug
```

## Support

If you encounter issues:
1. Check the troubleshooting section above
2. Verify all dependencies are installed
3. Run the integration script again
4. Check Cursor and Android Studio logs

Happy coding! 🚀
