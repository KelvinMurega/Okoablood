# OkoaBlood - Quick Run Script for Cursor
# This script sets up the environment and runs the app on your connected phone

Write-Host "Setting up environment..." -ForegroundColor Green

# Set Java Home (Android Studio JDK)
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Add ADB to PATH
$adbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools"
if (Test-Path $adbPath) {
    $env:PATH = "$adbPath;$env:PATH"
    Write-Host "ADB found" -ForegroundColor Green
} else {
    Write-Host "ADB not found. Please install Android SDK Platform Tools." -ForegroundColor Red
    exit 1
}

# Check for connected device
Write-Host ""
Write-Host "Checking for connected devices..." -ForegroundColor Yellow
$devices = adb devices
if ($devices -match "device$") {
    Write-Host "Device connected" -ForegroundColor Green
} else {
    Write-Host "No device found. Please connect your phone and enable USB debugging." -ForegroundColor Red
    exit 1
}

# Build and install
Write-Host ""
Write-Host "Building and installing app..." -ForegroundColor Yellow
.\gradlew.bat installDebug

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "App installed successfully!" -ForegroundColor Green
    
    # Launch the app
    Write-Host ""
    Write-Host "Launching app..." -ForegroundColor Yellow
    adb shell am start -n com.example.okoablood/.MainActivity
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "App launched successfully!" -ForegroundColor Green
        Write-Host "The app should now be running on your phone." -ForegroundColor Cyan
    } else {
        Write-Host "App installed but failed to launch automatically." -ForegroundColor Yellow
        Write-Host "Please launch it manually from your phone." -ForegroundColor Cyan
    }
} else {
    Write-Host ""
    Write-Host "Build failed. Check the error messages above." -ForegroundColor Red
    exit 1
}
