# Windsurf + Android Studio Integration Guide

This guide explains how to use Windsurf alongside Android Studio for the Adoption and Child Care App project.

## 🚀 Quick Start

### Option 1: Using the Batch Script
1. Double-click `open-in-android-studio.bat`
2. The script will automatically find and launch Android Studio with this project

### Option 2: Using PowerShell
1. Right-click `open-in-android-studio.ps1` and select "Run with PowerShell"
2. Or open PowerShell in the project directory and run: `.\open-in-android-studio.ps1`

### Option 3: Manual Setup
1. Open Android Studio
2. Choose "Open an existing project"
3. Navigate to this project folder
4. Select the root folder containing `build.gradle.kts`

## 🔧 Project Structure

```
adoption-and-child-care-main/
├── app/
│   ├── src/main/java/com/yourdomain/adoptionchildcare/
│   │   ├── MainActivity.kt
│   │   ├── MyApp.kt
│   │   ├── data/
│   │   ├── network/
│   │   ├── ui/compose/
│   │   └── utils/
│   └── build.gradle.kts
├── .windsurf/
│   ├── config.json
│   └── workspace.json
└── gradle/
```

## 🔗 GitHub Integration

- **Repository**: https://github.com/Brianmwanza-bit/adoption-and-child-care-main.git
- **Branch**: main
- **Git Status**: ✅ Connected and configured

## 🛠️ Development Workflow

### Using Windsurf for Code Editing
1. Use Windsurf for AI-assisted coding, refactoring, and code generation
2. Windsurf excels at:
   - Writing Kotlin code
   - Creating Jetpack Compose UI components
   - API integration
   - Database operations
   - Code documentation

### Using Android Studio for:
1. **Building and Running**: Use Android Studio's build system
2. **Debugging**: Android Studio's debugger is optimized for Android apps
3. **UI Preview**: Live preview of Jetpack Compose components
4. **Device Testing**: Emulator and device management
5. **Profiling**: Memory, CPU, and network profiling

## 📱 Build Commands

### From Command Line:
```bash
# Clean build
gradlew clean

# Build debug APK
gradlew assembleDebug

# Install on connected device
gradlew installDebug

# Run tests
gradlew test
```

### From Android Studio:
- **Build**: Build → Make Project (Ctrl+F9)
- **Run**: Run → Run 'app' (Shift+F10)
- **Debug**: Run → Debug 'app' (Shift+F9)

## 🔄 Sync Between Tools

### When working in Windsurf:
1. Make code changes in Windsurf
2. Save files (Ctrl+S)
3. Switch to Android Studio
4. Android Studio will automatically detect changes and prompt to sync

### When working in Android Studio:
1. Make changes in Android Studio
2. Save files
3. Windsurf will automatically detect file changes
4. Use Windsurf for AI assistance with the modified code

## 🎯 Best Practices

1. **Primary Development**: Use Windsurf for writing and editing code
2. **Testing & Debugging**: Use Android Studio for running and debugging
3. **Version Control**: Commit changes from either tool (both use the same Git repo)
4. **File Sync**: Both tools work on the same files, changes are reflected immediately

## 🚨 Troubleshooting

### Android Studio Not Found
- Install Android Studio from: https://developer.android.com/studio
- Update the paths in the launch scripts if installed in a custom location

### Gradle Sync Issues
- In Android Studio: File → Sync Project with Gradle Files
- Or run: `gradlew --refresh-dependencies`

### Git Issues
- Ensure you have Git installed and configured
- Check your GitHub authentication: `git config --list`

## 📞 Support

If you encounter any issues:
1. Check the Android Studio Event Log (bottom right)
2. Review Gradle build output
3. Ensure all dependencies are properly configured in `build.gradle.kts`

---

**Happy Coding! 🎉**

*This integration allows you to leverage the best of both worlds: Windsurf's AI capabilities and Android Studio's Android-specific tools.*
