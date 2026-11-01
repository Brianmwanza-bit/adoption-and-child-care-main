# 🎨 Theme, Styling & Color Standards

## 1. Overview
The **Adoption & Child Care Tracking System** enforces a clean, high-contrast **Light Theme** across all screens, cards, and sub-components.

---

## 2. Color Palette & Rules
- **Screen & Card Backgrounds:** Pure White (`Color.White` / `Color(0xFFF8F9FA)`). Dark mode overrides are disabled (`darkTheme = false`).
- **Header & Drawer Theme:** Modern Vibrant Blue (`Color(0xFF2196F3)` / `Color(0xFF1976D2)`).
- **Text & Typography:** High-contrast solid black (`Color.Black` / `Color(0xFF1A1A1A)`). Faint gray text for database values has been eliminated.
- **Footers & Actions:** Bottom blue bar removed in favor of clean card action items.
- **Role Dropdown:** Registration role dropdown (`ExposedDropdownMenu`) features a blue background (`Color(0xFF2196F3)`) with bold white text (`Color.White`).

---

## 3. Profile Photo & Biometric Hardware Standards
- **Containers:** Top header bar (`GreenHeader`) and drawer header (`DrawerHeader`) render the user's profile photo in real time via `RetrofitClient.getNormalizedPhotoModel(...)`.
- **Persistable Permissions:** `takePersistableUriPermission(uri, FLAG_GRANT_READ_URI_PERMISSION)` ensures photo URIs remain valid across reboots.
- **Hardware Biometrics:** `MainActivity` extends `FragmentActivity`, enabling Android's native `BiometricPrompt` for instant hardware fingerprint/face authentication.
