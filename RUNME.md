# Run the Switsh App

This guide explains how to run the Android app locally as a developer.

## Requirements

Before you start, make sure you have:

- Android Studio installed
- Android SDK installed and configured
- Java 17 or compatible JDK available
- A connected Android device or an emulator

## 1. Open the project

Open the project root folder in Android Studio:

```text
/home/reyliwe/Workspace/switsh-app
```

or open the folder from the file menu:

- File > Open
- Select the `switsh-app` directory

## 2. Sync Gradle

Android Studio will prompt you to sync the Gradle files. Accept the prompt.

If needed, run:

```bash
./gradlew assembleDebug
```

from the project root.

## 3. Start an emulator or connect a device

Choose one of the following:

- Start an Android emulator in Android Studio
- Connect a physical Android device with USB debugging enabled

## 4. Run the app

From Android Studio:

- Select the app module
- Choose a device/emulator
- Click Run

Or from the command line:

```bash
./gradlew installDebug
```

Then launch the APK on the emulator or connected device.

## 5. Common troubleshooting

### Gradle sync issues

Ensure the Android SDK path is configured correctly in:

- `local.properties`
- Android Studio SDK Manager

### Java mismatch

Use a supported JDK version for Android builds. Java 17 is the safest choice for this project setup.

### Emulator not detected

Verify that:

- the emulator is running
- virtualization is enabled on your machine
- the device is listed in `adb devices`

## Useful commands

```bash
./gradlew clean
./gradlew assembleDebug
./gradlew installDebug
```

## Notes

This app is a UI prototype and does not require a backend service to run locally. The screens are implemented directly in Kotlin and XML layouts.
