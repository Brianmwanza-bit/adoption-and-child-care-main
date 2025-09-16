# App Icon, Splash Screen, and Branding Instructions

## 1. App Icon
- Use `src/icons/c7.jpg` as your app icon image.
- Convert `c7.jpg` to PNG format and resize to 512x512 px (recommended) using any image editor.
- In Android Studio, use the Image Asset tool:
  - Right-click `res` > New > Image Asset
  - Select the converted `c7.png` as the source image.
  - This will generate all required icon sizes in `app/src/main/res/mipmap-*`.
- Replace any existing `ic_launcher.png` and `ic_launcher_round.png` with the generated icons if needed.

## 2. Splash Screen
- Android 12+: Uses `splashscreen` API and `ic_launcher_foreground` by default.
- For older versions, edit `app/src/main/res/drawable/splash.xml` or `launch_background.xml`.
- To customize:
  - Change the background color in `res/values/colors.xml` (look for `colorPrimary` or `splash_background`).
  - Replace the logo in `res/drawable` or via the Image Asset tool.
- Recommended logo size: 200x200 px (centered)

## 3. App Name
- Change the app name in `app/src/main/res/values/strings.xml`:
  - Edit `<string name="app_name">Adoption & Child Care</string>`

## 4. Theme/Brand Colors
- Edit `app/src/main/res/values/colors.xml` and `themes.xml` to set your brand colors.
- You can also customize fonts in `res/font/` and `themes.xml`.

## 5. Test Your Branding
- Build and run the app on a real device to verify the icon, splash, and colors.

---

**For more details, see the official [Android icon guide](https://developer.android.com/guide/practices/ui_guidelines/icon_design_launcher) and [splash screen guide](https://developer.android.com/develop/ui/views/launch/splash-screen).** 