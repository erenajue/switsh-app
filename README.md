# Switsh Mobile App

Switsh is a Kotlin Android prototype based on the interactive Switsh mobile experience. The current UI implements the launch screen from the prototype's **Démarrage** section: a teal gradient, Switsh brand mark, USD/CDF banking tagline, and three launch progress indicators.

The app is being rebuilt from this starting point. Other screens and navigation flows have not yet been implemented.

## Tech stack

- Kotlin and Android SDK
- AndroidX AppCompat and View Binding
- Gradle

## Notes

This is a UI prototype. It does not include live banking integrations, authentication flows, or transaction processing.

## Onboarding API client

`com.switsh.clients.OnboardingApiClient` (Ktor + OkHttp + Gson) implements the operations described in
`app/src/main/java/com/switsh/clients/onboarding-api.yml`.

- A `tokenProvider: suspend () -> String` supplies a short-lived (≤ 7 min) bearer token for each request.
- Requests use 10-second connect, request, and socket timeouts; HTTPS is enforced except for loopback hosts.
- Writes send `X-Correlation-Id` and an `Idempotency-Key` (generated automatically unless supplied).
- Non-2xx responses throw `OnboardingApiException` with the RFC 9457 `Problem` or legacy `error`.

## License

This project is provided as a prototype for educational and design demonstration purposes.

## Onboarding API client

`com.switsh.clients.OnboardingApiClient` (Ktor + OkHttp + Gson) implements every operation of
`app/src/main/java/com/switsh/clients/onboarding-api.yml`.

- Constructed with a `tokenProvider: suspend () -> String`, called on every request (tokens are short-lived, ≤ 7 min) and sent as `Authorization: Bearer`.
- 10 s connect/request/socket timeouts; HTTPS enforced (HTTP only for loopback hosts).
- Writes send `X-Correlation-Id`; application endpoints also send `Idempotency-Key` (auto-generated UUIDs unless supplied).
- Non-2xx responses throw `OnboardingApiException` carrying the RFC 9457 `Problem` or legacy `error`.
