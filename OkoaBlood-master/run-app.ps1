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
    Write-Host "✓ ADB found" -ForegroundColor Green
} else {
    Write-Host "✗ ADB not found. Please install Android SDK Platform Tools." -ForegroundColor Red
    exit 1
}

# Check for connected device
Write-Host "`nChecking for connected devices..." -ForegroundColor Yellow
$devices = adb devices
if ($devices -match "device$") {
    Write-Host "✓ Device connected" -ForegroundColor Green
} else {
    Write-Host "✗ No device found. Please connect your phone and enable USB debugging." -ForegroundColor Red
    exit 1
}

# Build and install
Write-Host "`nBuilding and installing app..." -ForegroundColor Yellow
.\gradlew.bat installDebug

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✓ App installed successfully!" -ForegroundColor Green
    Write-Host "The app should launch on your phone automatically." -ForegroundColor Cyan
} else {
    Write-Host "`n✗ Build failed. Check the error messages above." -ForegroundColor Red
}

