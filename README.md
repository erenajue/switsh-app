# Switsh Mobile App

Switsh is a Kotlin Android prototype inspired by the interactive Switsh mobile banking experience. This project recreates the core UX flows of the product in a mobile app interface, with screens for account overview, card management, transfers, budgeting, support, and profile management.

## Overview

This app is intentionally focused on UI/UX implementation rather than full backend connectivity. It demonstrates a polished, customer-facing banking experience in a mobile layout, designed to mirror the prototype available at the Switsh product demo.

## Included Features

- Nine-screen onboarding and KYC/AML flow, including identity, personal, address, and business verification
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

## Onboarding API client

`com.switsh.clients.OnboardingApiClient` (Ktor + OkHttp + Gson) implements every operation of
`app/src/main/java/com/switsh/clients/onboarding-api.yml`.

- Constructed with a `tokenProvider: suspend () -> String`, called on every request (tokens are short-lived, ≤ 7 min) and sent as `Authorization: Bearer`.
- 10 s connect/request/socket timeouts; HTTPS enforced (HTTP only for loopback hosts).
- Writes send `X-Correlation-Id`; application endpoints also send `Idempotency-Key` (auto-generated UUIDs unless supplied).
- Non-2xx responses throw `OnboardingApiException` carrying the RFC 9457 `Problem` or legacy `error`.
