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
- Full request/response debug logging is enabled for `http://localhost:8080/*`; authorization headers are redacted.
- Writes send `X-Correlation-Id`; application endpoints also send `Idempotency-Key` (auto-generated UUIDs unless supplied).
- Non-2xx responses throw `OnboardingApiException` carrying the RFC 9457 `Problem` or legacy `error`.

## Personal information

On the "Informations personnelles" screen, Continue sends the form to `OnboardingApiClient.savePersonalProfile` (date converted to ISO, gender/nationality/source of funds mapped to API enums, optional monthly income mapped to an income band). The app only moves on once the profile is saved.

## Identity document

On the "Pièce d'identité" screen, Validate checks the document number, issue/expiry dates, front photo and selfie (back photo optional). Each file is uploaded via `createUploadSession` + PUT, then `OnboardingApiClient.attachIdentityDocument` is called with the upload ids; navigation happens only on success. OCR is not implemented.

## Application submission

On the "Justificatif de domicile" screen, the submit button calls `OnboardingApiClient.submitApplication` and only navigates to the status screen once the API confirms.

A proof of address (JPEG, PNG or PDF, max 10 MB) is picked with the system file picker. On submit it is uploaded through `createUploadSession` followed by a PUT to the returned upload URL. If no document is attached, the "use current location" toggle requests location permission, reverse-geocodes the position and stores it as the current address in the local SQLite `current_address` table. Without a document or a location, submission is refused.

## Business KYB

The "Entreprise / PME (KYB)" screen validates the company identifiers, incorporation date, UBO ownership percentages (maximum 100%), and required legal evidence. Statutes, the legal representative's identity document, and UBO supporting documents are uploaded through constrained upload sessions; company, document, and representative/UBO details are saved to the legacy onboarding sections before the application is submitted. The screen advances only after the API confirms each step.

## Verification status

The verification screen fetches the current application with `getApplication` whenever it resumes, then displays the returned compliance status, workflow state, and KYC tier. Values remain marked unavailable if the request fails or the API omits them; the screen does not imply a verified state before the API confirms it.

When the API supplies an `amlAssessment`, the same screen displays localized initial screening, ongoing-monitoring, risk-level, and decision outcomes. Missing or unrecognized AML values remain unavailable; the client does not derive AML results from compliance status. The assessment object is optional because the current backend does not yet implement screening.

## KYC transaction limit

The P2P payment demo blocks a transfer when it exceeds the displayed remaining monthly KYC limit and explains that it was not sent; the dialog links to verification status. Until a payment API provides authoritative limit and rejection data, the displayed USD 1,180 remaining limit and USD/CDF conversion rate (2,850) are explicitly demo placeholders, not production limits.

## Unlock

The PIN created on the "Authentification & sécurité" screen is stored as a salted BCrypt hash. Existing PBKDF2 hashes are verified and upgraded to BCrypt after a successful PIN entry. Without a saved PIN the app never shows the unlock screen: it restarts onboarding at language selection, so a registered user can only connect with a PIN. If the Face ID or fingerprint toggle is on, the platform biometric prompt is shown on startup (unlock screen) and the biometric key is shown on the PIN pad; otherwise the 4-digit PIN is required.
