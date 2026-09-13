# Switsh Mobile App

Switsh is a Kotlin Android prototype inspired by the interactive Switsh mobile banking experience. This project recreates the core UX flows of the product in a mobile app interface, with screens for account overview, card management, transfers, budgeting, support, and profile management.

## Overview

This app is intentionally focused on UI/UX implementation rather than full backend connectivity. It demonstrates a polished, customer-facing banking experience in a mobile layout, designed to mirror the prototype available at the Switsh product demo.

## Included Features

- Account dashboard with balance summary
- Card overview and controls
- Payment/transfer flow
- Monthly budget management
- In-app support chat experience
- User profile and settings
- Material Design styling aligned with a fintech aesthetic

## Project Structure

```text
switsh-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/switsh/app/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── SwitshPagerAdapter.kt
│   │   │   │   ├── DashboardFragment.kt
│   │   │   │   ├── CardFragment.kt
│   │   │   │   ├── TransferFragment.kt
│   │   │   │   ├── BudgetFragment.kt
│   │   │   │   ├── SupportFragment.kt
│   │   │   │   └── ProfileFragment.kt
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       ├── drawable/
│   │   │       └── values/
│   └── build.gradle.kts
├── build.gradle.kts
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── gradle.properties
├── local.properties
├── README.md
├── RUNME.md
└── .gitignore
```

## Tech Stack

- Kotlin
- Android SDK
- Jetpack ViewPager2
- AndroidX / Material Components
- Gradle

## Design Focus

The app follows a clean fintech design language:

- Green and white banking palette
- Rounded cards and modern panels
- Strong readability and spacing
- Mobile-first interactions
- Clear action buttons and segmented navigation

## Notes

This is a front-end prototype intended for product design and UX validation. It does not include live banking integrations, authentication backends, or real transaction processing.

## License

This project is provided as a prototype for educational and design demonstration purposes.
