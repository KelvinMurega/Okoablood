# Running OkoaBlood in Cursor IDE

## Quick Start

### Option 1: Use the Script (Easiest)

Just run:
```powershell
.\run-app.ps1
```

This script will:
- Set up Java and ADB automatically
- Check for connected devices
- Build and install the app on your phone

### Option 2: Manual Setup

If you prefer to run commands manually, here's what you need:

#### 1. Set up environment variables (run once per terminal session):

```powershell
# Set Java Home
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Add ADB to PATH
$adbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools"
$env:PATH = "$adbPath;$env:PATH"
```

#### 2. Verify your phone is connected:

```powershell
adb devices
```

You should see your device listed.

#### 3. Build and install:

```powershell
.\gradlew.bat installDebug
```

---

## Troubleshooting

### "JAVA_HOME is not set"
- Make sure Android Studio is installed
- The script automatically sets it to: `C:\Program Files\Android\Android Studio\jbr`
- If Android Studio is in a different location, update the path in `run-app.ps1`

### "SDK location not found"
- The `local.properties` file should already be created with the SDK path
- If not, create it with: `sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk`
- Replace `YOUR_USERNAME` with your Windows username

### "No device found"
1. Connect your phone via USB
2. Enable USB Debugging:
   - Settings → About Phone → Tap "Build Number" 7 times
   - Settings → Developer Options → Enable "USB Debugging"
3. Allow USB debugging when prompted on your phone
4. Run `adb devices` to verify

### Build Errors
- Make sure you're in the `OkoaBlood-master` directory
- Check that all dependencies are downloaded (first build may take longer)
- Try: `.\gradlew.bat clean` then `.\gradlew.bat installDebug`

---

## Tips

- **Keep your phone unlocked** while debugging
- **First build takes longer** - be patient!
- **Subsequent builds are faster** thanks to Gradle caching
- The app **automatically launches** after installation

---

## What Gets Installed?

The `installDebug` command:
1. Compiles your Kotlin/Java code
2. Packages resources and assets
3. Creates a debug APK
4. Installs it on your connected phone
5. Launches the app automatically

---

## Need Help?

Check the build output for specific error messages. Most common issues:
- Missing dependencies → Run `.\gradlew.bat build` first
- Device not authorized → Check phone for USB debugging prompt
- SDK issues → Verify `local.properties` file exists

Happy coding! 🚀

