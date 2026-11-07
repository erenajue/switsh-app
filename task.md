# Switsh mobile app — task list

Derived from `specifications.md` (v4.1). `[x]` = covered in the app today, `[ ]` = to do.
"UI only" means the screen exists from the prototype but is not wired to the API or any logic.
Back-office, compliance-engine and infrastructure requirements (Domain 7, RBAC, most of section 5.1, TR-*) are server-side and not part of this Android app; they are listed at the end for traceability.

## 1. Domain 1 — Onboarding, security & compliance

### Registration and authentication
- [x] BR-ONB-01 Registration screen (phone, email, terms) calling `registerApplication` (`onb-inscription`)
- [x] BR-ONB-01 SMS OTP screen verified with `verifyContact(PHONE)`; Resend re-issues the challenge on the same application (`onb-otp`)
- [x] BR-ONB-01 Email verification screen (64-character code sent as the `token`) verified with `verifyContact(EMAIL)`; blocks progress to profile until completed (`onb-mail-otp`)
- [ ] BR-ONB-01 Password field on the registration screen (spec lists it; not in the API request)
- [x] BR-ONB-02 PIN creation + confirmation (4 digits, trivial PINs rejected) (`onb-auth-security`)
- [x] BR-ONB-02 Biometric activation (Face ID / fingerprint toggles) enabling biometric unlock at startup
- [x] BR-ONB-02 PIN required to unlock once registered; onboarding restarts if no PIN was ever saved
- [x] Unlock screen personalized with the name from the last-connection SQLite DB
- [ ] BR-ONB-02 Strong customer authentication (PIN/biometric) before sensitive actions (payments, limits, card changes)
- [ ] 4/6-digit PIN choice (only 4 digits supported)
- [x] Spec NFR: hash PIN with BCrypt; existing PBKDF2 PIN hashes are upgraded on successful verification
- [ ] BR-ONB-03 SIM identity match (ARPTC)

### Profile and KYC/KYB
- [x] Customer profile selection screen calling `selectProfile`; no navigation unless confirmed, "temporarily unavailable" error otherwise (`onb-profile-select`)
- [x] BR-ID-01 Identity document screen (`onb-id-particulier`): files picked, uploaded via `createUploadSession`, then `attachIdentityDocument` (capture via file picker, no camera yet)
- [ ] BR-ID-01 OCR of CENI / ONIP-NIN / passport / resident card
- [ ] BR-ID-02 Liveness selfie and face match
- [x] Personal information screen calling `savePersonalProfile` on Continue; no navigation unless saved, "temporarily unavailable" error otherwise (`onb-infos-perso`)
- [x] BR-ID-03 Proof-of-address screen: document upload via `createUploadSession` + PUT, geolocation fallback stored in SQLite, submit calls `submitApplication` (`onb-justif-domicile`)
- [x] BR-KYB-01 Business KYB screen (`onb-kyb`): validates and submits company details, RCCM/NIF, statutes, legal representative, and UBOs with supporting documents
- [x] Verification status screen driven by `getApplication` instead of static content (`onb-status`)
- [x] BR-TIER-01 Display API-driven KYC status and tier (levels 1/2/3) on `onb-status`
- [x] BR-TIER-02 Explicit blocking message when a P2P transaction exceeds the KYC threshold (demo limit placeholder pending payment API)
- [x] BR-AML-01..04 Display optional server-reported AML screening, ongoing-monitoring, risk, and decision outcomes on `onb-status`; missing/unrecognized values show unavailable (screening engine remains server-side)

## 2. Domain 2 — Core banking
- [ ] BR-CB-01 CDF and USD wallets in a single profile
- [ ] BR-CB-02 Show local banking identifier / IBAN
- [ ] BR-CB-03 Theme-based sub-accounts and reserve vaults
- [ ] BR-CB-04 Real-time history with currency and conversion rate
- [ ] BR-CB-05 Merchant enrichment in history (name, category, location)
- [ ] BR-CB-06 PDF and CSV account statements
- [ ] BR-CB-07 Real-time and available balance

## 3. Domain 3 — Cards
- [ ] BR-CARD-01 Physical card request (Visa / Mastercard)
- [ ] BR-CARD-02 Permanent virtual card
- [ ] BR-CARD-03 Ephemeral virtual card with dynamic CVC
- [ ] BR-CARD-04 Mobile wallet compatibility
- [ ] BR-CARD-05 Block physical card issuance when balance is below the threshold
- [ ] BR-CARD-06 Instant lock / unlock
- [ ] BR-CARD-07 ATM and POS limits by currency and channel
- [ ] BR-CARD-08 Channel activation (international, web, ATM)

## 4. Domain 4 — Payments
- [ ] BR-PAY-01 Mobile Money cash-in / cash-out
- [ ] BR-PAY-02 External bank transfer (BCC ATS/RIP)
- [ ] BR-FX-01 Instant USD/CDF conversion with a short rate-validity window
- [ ] BR-FX-02 Conversion history
- [ ] BR-INTL-01 International send / receive
- [ ] BR-P2P-01 P2P transfer by phone, user ID or QR
- [ ] BR-P2P-02 Merchant payment by static / dynamic QR
- [ ] BR-P2P-03 NFC card payment
- [ ] BR-P2P-04 Bill splitting
- [ ] BR-P2P-05 Shared wallet / group cagnotte
- [ ] BR-FEE-11 Show fees transparently before confirmation

## 5. Domain 5 — PFM and savings
- [ ] BR-PFM-01 Automatic expense categorisation, consolidated USD/CDF view
- [ ] BR-PFM-02 Budget-exceeded alerts
- [ ] BR-PFM-03 Automatic round-up to a savings vault
- [ ] BR-PFM-04 Recurring transfers to savings sub-accounts

## 6. Domain 6 — Support
- [ ] BR-SUP-01 Chatbot and FAQ in all active languages
- [ ] BR-SUP-02 Secure live chat
- [ ] BR-SUP-03 Open, track and resolve disputes

## 7. Multilingual (section 5.3)
- [x] BR-LNG-01 Six languages (fr, en, ln, kg, lua, sw) in `ScreenLabels`
- [x] BR-LNG-05 Language chosen at first launch before registration (`start-language`)
- [x] BR-LNG-06 Selected language persisted locally (`AppState`)
- [x] BR-LNG-14 No hard-coded visible text; labels resolved through `{{key}}`
- [x] BR-LNG-18 Language packs available offline (bundled in the app)
- [ ] BR-LNG-07 Change language at any time without logout (settings screen not implemented)
- [ ] BR-LNG-06 Propagate language to the user profile on the server
- [ ] BR-LNG-08 Native-speaker review of ln, kg, lua, sw translations (currently unreviewed)
- [ ] BR-LNG-04 Legal and financial documents shown in French
- [ ] BR-LNG-15 Locale-aware number formatting preserving the numeric value
- [ ] BR-LNG-17 / QC-LNG-09 Long-text handling tested up to +40%
- [ ] BR-LNG-09..13 Remote language activation and fallback (needs back-office config)

## 8. Technical requirements (section 12) and non-functional
- [x] Ktor REST client with 10 s timeouts, bearer token on every call, `correlationId` on writes, idempotency keys
- [x] Full debug logging of HTTP calls to `http://localhost:8080/*` (Authorization header masked)
- [x] SQLite internal database (last connection, profile name)
- [ ] REQ-001 / REQ-004 Persist all section data in an encrypted local DB (AES-256-GCM, keys in Keystore); the PIN hash and registration data currently sit in SharedPreferences
- [ ] REQ-002 Background sync with exponential backoff (2 s start, 32 s max, 5 attempts)
- [ ] REQ-003 Single atomic submission of all sections
- [ ] REQ-005 Resume onboarding within 30 days at the last unsynced section (today a restart before the PIN step goes back to language selection)
- [ ] REQ-006 Client-side schema validation on every onboarding screen (done for email, phone, OTP and PIN only)
- [x] REQ-007 Idempotency keys on write calls
- [ ] REQ-008 Server-authoritative conflict resolution
- [ ] REQ-009 Opt-in telemetry
- [ ] REQ-010 Offline mode for the full onboarding flow (registration and OTP currently require internet)
- [ ] REQ-011 Delete synced data 7 days after submission
- [ ] REQ-012 Rate limiting of the sync queue (max 3 concurrent, throttle on 429)
- [ ] Replace the insecure `allowInsecureHttp` flag and the `localhost` base URL before release
- [ ] TLS 1.3 only for transit
- [ ] Push notifications under 2 s

## 9. Out of scope for the mobile app (server / back-office / programme)
Kept for traceability; not planned here.
- BR-AML-01..04 screening engines, BR-BO-01..10 back-office, BR-RBAC-01..04, BR-FEE-01..10 tariff configuration
- TR-01..05 transition requirements (company setup, PCI-DSS certification, pilot, training, partner integration)
