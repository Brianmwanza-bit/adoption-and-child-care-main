cd android
gradlew.bat assembleDebug
gradlew.bat installDebug
adb shell am start -n com.example.adoption_and_childcare/.MainActivity
