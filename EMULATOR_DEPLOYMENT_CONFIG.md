# 📱 EMULATOR DEPLOYMENT CONFIGURATION GUIDE

## **Configuration Updated for Virtual Emulator** ✅

---

## 🔧 **What Was Changed**

### **1. API Base URL (AppSettings.kt)** ✅

**Before:**
```kotlin
var apiBaseUrl: String
    get() = prefs.getString(KEY_API_BASE_URL, "http://192.168.43.197:50000/") 
```

**After:**
```kotlin
var apiBaseUrl: String
    get() = prefs.getString(KEY_API_BASE_URL, "http://10.0.2.2:50000/")
```

**Why:** 
- `10.0.2.2` is the Android emulator's special alias to your host machine's `localhost`
- Physical devices need your actual IP (e.g., `192.168.43.197`)
- Emulator needs `10.0.2.2` to access the backend running on your computer

---

## 🎯 **Configuration Options**

### **Option 1: Virtual Emulator (Recommended for Testing)**

**Backend URL:** `http://10.0.2.2:50000/`

**Steps:**
1. ✅ Already configured in `AppSettings.kt`
2. Start your backend server on port 50000
3. Build and run the app on emulator
4. App will automatically connect to backend

**Example:**
```
Your Computer: localhost:50000
     ↓
Emulator sees it as: 10.0.2.2:50000
```

---

### **Option 2: Physical Device**

**Backend URL:** `http://YOUR_IP:50000/`

**Steps:**
1. Find your computer's IP address:
   ```powershell
   ipconfig
   ```
   Look for `IPv4 Address` under your Wi-Fi adapter (e.g., `192.168.43.197`)

2. Update the URL in the app's Settings screen:
   - Open app → Settings
   - Change "API Base URL" to `http://192.168.43.197:50000/`
   - Save settings

3. Make sure:
   - ✅ Phone and computer are on the same Wi-Fi network
   - ✅ Backend server is running
   - ✅ Firewall allows port 50000

---

### **Option 3: Using localhost (Auto-Mapped)**

**Backend URL:** `http://localhost:50000/` or `http://127.0.0.1:50000/`

**Smart Auto-Mapping:**
The app automatically converts:
- `localhost` → `10.0.2.2` (for emulator)
- `127.0.0.1` → `10.0.2.2` (for emulator)

This is handled in `RetrofitClient.kt` lines 32-34.

---

## 🚀 **Pre-Deployment Checklist**

### **Backend Server**
- [ ] Backend server is running on port 50000
- [ ] Test backend: Open browser → `http://localhost:50000/`
- [ ] Check backend logs for errors

### **Android Emulator**
- [ ] Emulator is created and running
- [ ] Emulator has internet access
- [ ] Emulator can reach host machine (test with `10.0.2.2`)

### **App Configuration**
- [x] API URL set to `http://10.0.2.2:50000/`
- [x] `usesCleartextTraffic="true"` in AndroidManifest (✅ already set)
- [x] INTERNET permission granted (✅ already set)

### **Network**
- [ ] Backend firewall allows connections on port 50000
- [ ] No proxy blocking local connections
- [ ] Backend is binding to `0.0.0.0` or `localhost` (not external IP only)

---

## 📝 **How to Start Backend Server**

### **Windows (PowerShell):**
```powershell
cd backend
node server.js
```

### **Expected Output:**
```
Server running on port 50000
Database connected successfully
```

---

## 🔍 **Testing Backend Connection**

### **Test 1: Browser Test**
1. Open browser on your computer
2. Go to: `http://localhost:50000/`
3. Should see API response or welcome message

### **Test 2: Emulator Browser Test**
1. Open browser in the emulator
2. Go to: `http://10.0.2.2:50000/`
3. Should see same response as computer browser

### **Test 3: App Connection Test**
1. Build and run app on emulator
2. Open app
3. Check logcat for connection logs:
   ```
   adb logcat | grep "Retrofit"
   ```

---

## 🛠️ **Troubleshooting**

### **Problem: "Connection Refused" or "Unable to resolve host"**

**Solutions:**
1. ✅ Check backend is running: `node server.js`
2. ✅ Verify port 50000 is not blocked
3. ✅ Try `http://10.0.2.2:50000/` in emulator browser
4. ✅ Check backend logs for startup errors

### **Problem: Backend starts but app can't connect**

**Solutions:**
1. ✅ Make sure backend binds to `0.0.0.0` or `localhost`
2. ✅ Check Windows Firewall settings
3. ✅ Verify no antivirus blocking port 50000
4. ✅ Restart both backend and emulator

### **Problem: App crashes on startup**

**Solutions:**
1. ✅ Check logcat for errors: `adb logcat`
2. ✅ Verify API URL is correct in Settings
3. ✅ Clear app data and try again
4. ✅ Rebuild app: `.\gradlew clean assembleDebug`

---

## 📊 **Configuration Summary**

| Setting | Value | Location |
|---------|-------|----------|
| **API Base URL** | `http://10.0.2.2:50000/` | `AppSettings.kt` |
| **API Timeout** | 30 seconds | `AppSettings.kt` |
| **Retry Count** | 3 | `AppSettings.kt` |
| **Cleartext Traffic** | `true` | `AndroidManifest.xml` |
| **Internet Permission** | ✅ Granted | `AndroidManifest.xml` |

---

## 🎓 **Understanding Emulator Networking**

### **Special Emulator Addresses:**

| Address | Meaning |
|---------|---------|
| `10.0.2.2` | Host machine's localhost (your computer) |
| `10.0.2.15` | Emulator's own IP |
| `127.0.0.1` | Emulator's own loopback (NOT your computer!) |
| `localhost` | Emulator's own loopback (NOT your computer!) |

### **Common Mistake:**
❌ Using `localhost` or `127.0.0.1` in emulator → Points to emulator itself  
✅ Using `10.0.2.2` in emulator → Points to your computer

---

## 🚦 **Quick Start Commands**

### **1. Start Backend:**
```powershell
cd C:\Users\Lydia mwanza\StudioProjects\adoption-and-child-care-main\backend
node server.js
```

### **2. Build App:**
```powershell
cd C:\Users\Lydia mwanza\StudioProjects\adoption-and-child-care-main\android
.\gradlew clean assembleDebug
```

### **3. Install on Emulator:**
```powershell
.\gradlew installDebug
```

### **4. Run App:**
- Open app from emulator's app drawer
- Or use: `adb shell am start -n com.example.adoption_and_childcare/.MainActivity`

---

## 📱 **In-App Settings**

Once the app is running, you can change settings from within the app:

1. Open **Settings** screen
2. Find **API Base URL**
3. Change to:
   - **Emulator:** `http://10.0.2.2:50000/`
   - **Physical Device:** `http://YOUR_IP:50000/`
4. Save and restart app

---

## ✅ **Verification Steps**

After deploying to emulator:

1. **Open app** → Should load without crashes
2. **Navigate to any screen** → Should load data from local DB
3. **Check Settings** → Verify API URL is `http://10.0.2.2:50000/`
4. **Trigger sync** → Should connect to backend successfully
5. **Check logcat** → No connection errors

---

## 🎯 **Next Steps**

1. ✅ Configuration updated
2. ⏳ Start backend server
3. ⏳ Build app
4. ⏳ Deploy to emulator
5. ⏳ Test all screens
6. ⏳ Verify API connectivity

---

*Configuration Status: ✅ UPDATED FOR EMULATOR*  
*Date: June 12, 2026*  
*Default API URL: http://10.0.2.2:50000/*  
*Ready for Deployment: YES ✅*
