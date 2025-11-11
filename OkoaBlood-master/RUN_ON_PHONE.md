# Running OkoaBlood on Your Phone

## Quick Setup Guide

### Step 1: Enable USB Debugging on Your Phone

1. **Open Settings** → **About Phone**
2. Tap **Build Number** 7 times (you'll see "You are now a developer!")
3. Go back to **Settings** → **Developer Options**
4. Enable **USB Debugging**
5. (Optional) Enable **Stay Awake** (keeps screen on while charging)

### Step 2: Connect Your Phone

1. Connect your phone to your laptop via USB cable
2. On your phone, when prompted, tap **"Allow USB Debugging"**
3. Check **"Always allow from this computer"** (optional but recommended)

### Step 3: Verify Connection

Open terminal/command prompt in the project folder and run:

```bash
adb devices
```

You should see your device listed, for example:
```
List of devices attached
ABC123XYZ    device
```

### Step 4: Run the App

#### Option A: Using Android Studio (Recommended)

1. Open the project in **Android Studio**
2. Wait for Gradle sync to complete
3. Select your phone from the **device dropdown** (top toolbar, next to the run button)
4. Click the **Run** button (green play icon) or press `Shift + F10`
5. The app will build, install, and launch on your phone!

#### Option B: Using Command Line

**Windows:**
```bash
gradlew.bat installDebug
```

**Mac/Linux:**
```bash
./gradlew installDebug
```

The app will install and launch automatically on your phone.

---

## Troubleshooting

### Device Not Detected?

1. **Try a different USB cable** - Some cables are charge-only
2. **Try a different USB port** on your laptop
3. **Revoke USB debugging authorizations** on your phone:
   - Settings → Developer Options → Revoke USB debugging authorizations
   - Then reconnect and allow again
4. **Restart ADB:**
   ```bash
   adb kill-server
   adb start-server
   adb devices
   ```

### "Unauthorized" Device?

- Check your phone screen for the USB debugging prompt
- Tap **"Allow"** when prompted
- Make sure you check "Always allow from this computer"

### Build Errors?

- Make sure **Android SDK** is installed
- Check that your phone's Android version is **7.0 (API 24) or higher**
- Try **File → Invalidate Caches / Restart** in Android Studio

### App Crashes on Launch?

- Check **Logcat** in Android Studio for error messages
- Make sure **Firebase** is properly configured (if using Firebase features)
- Check that your phone meets the minimum requirements (Android 7.0+)

---

## Requirements

- **Android Phone** with Android 7.0 (API 24) or higher
- **USB Cable** (data cable, not charge-only)
- **Android Studio** installed on your laptop
- **USB Drivers** (usually installed automatically, but may need manual installation for some devices)

---

## First Time Setup Tips

1. **Keep your phone unlocked** while debugging
2. **Enable "Stay Awake"** in Developer Options to prevent screen timeout
3. **Use a reliable USB cable** - charge-only cables won't work
4. **Allow USB debugging** when prompted on your phone

---

## Need Help?

- Check Android Studio's **Logcat** for error messages
- Verify your phone appears in `adb devices`
- Make sure your phone's Android version is compatible (minSdk 24)

Happy coding! 🚀

