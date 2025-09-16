This folder contains VS Code tasks and launch configurations to build and deploy the Android app via Gradle.

Tasks:
- build-debug: runs `./gradlew assembleDebug`
- install-debug: runs `./gradlew installDebug` (requires device/emulator)

Launch:
- Attach to Android Studio/Gradle process for debugging is not provided; use Android Studio for full device debugging.

How to use:
1. Open this workspace in VS Code.
2. Run Tasks: Run Task... and pick `build-debug`.
3. To install, ensure a device/emulator is connected and run `install-debug`.
