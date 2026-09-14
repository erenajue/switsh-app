# Switsh Business Requirements Specification

Version: 4.1
Date: 2026-08-21
Project: Switsh

## 1. Purpose and scope

This document captures the extracted business requirements for the Switsh mobile banking and financial services platform, as described in the BRD. It consolidates the functional, regulatory, operational, and quality requirements across all business domains, and forms the basis for product, UX, engineering, and compliance implementation.

The solution covers seven functional domains plus three cross-cutting modules:

| Domain / module | Description |
| --- | --- |
| Domain 1 | Onboarding, Security & Compliance (KYC/AML) |
| Domain 2 | Core Banking & Multi-currency Accounts (USD/CDF) |
| Domain 3 | Card Management (physical and virtual) |
| Domain 4 | Payment Engine & Interoperability (RDC & international) |
| Domain 5 | Personal Finance Management (PFM) & Savings |
| Domain 6 | Customer Support & In-App Service |
| Domain 7 | Back-office Administration, Compliance & Infrastructure |
| Cross-cutting module | Tarification, fees and admin configuration |
| Cross-cutting module | User roles and access control (RBAC) |
| Cross-cutting module | Multilingual support and localization |

Out of scope for Phase 1:

| Item | Scope exclusion |
| --- | --- |
| 1 | Credit, overdraft or regulated savings products |
| 2 | Cards in currencies other than USD/CDF |
| 3 | Expansion beyond the RDC Corridor Ouest |

## 2. Business need

Switsh must enable individuals and businesses in the DRC to identify themselves, open a multi-currency account compliant with BCC requirements, manage digital payments, cards, transfers, and budgeting, while ensuring secure operations, auditable controls, and compliance with BCC, CENAREF, and Law No. 22/068 on AML/FT-PADM.

## 3. Stakeholders and affected users

| Stakeholder | Role in the solution |
| --- | --- |
| Client particulier | Individual customer using retail banking and wallet features |
| Client entreprise / PME | Business client using account, payments, and operational services |
| Marchand affilié | Merchant accepting payments and benefiting from merchant payment flow |
| BCC (Bank of the Congo) | Regulator and banking partner for compliance and settlement requirements |
| CENAREF | Financial intelligence reporting and AML oversight |
| ARPTC | Telecom identity and SIM-related regulatory authority |
| Switsh compliance team | AML, KYC, and operational risk management |
| Customer support team | Service, dispute, escalation, and customer assistance |
| Technical/product team | Product management, configuration, operations, and platform governance |
| Card issuers | Physical and virtual card network and provisioning partners |
| Mobile Money operators | M-Pesa, Orange Money, Airtel Money, Africell Money integration |
| International transfer operators | SWIFT, RIA, Western Union, MoneyGram and similar partners |

## 4. Functional requirements by domain

### 4.1 Domain 1 — Onboarding, Security & Compliance (KYC/AML)

#### Business requirements

| ID | Requirement |
| --- | --- |
| BR-ONB-01 | Mobile-only registration with OTP validation by SMS and email confirmation. Account creation only after OTP + valid email. |
| BR-ONB-02 | Strong customer authentication (SCA) using biometrics or 4/6-digit PIN. Sensitive actions require authentication. |
| BR-ONB-03 | Match SIM identity with the registered customer account, in line with ARPTC rules. |
| BR-ID-01 | Capture and OCR of accepted RDC identity documents: CENI, ONIP/NIN, biometric passport, or resident card. |
| BR-ID-02 | Real-time liveness validation compared with identity document photo. |
| BR-ID-03 | Domestic proof-of-address collection: residence attestation, REGIDESO/SNEL invoice, certified geolocation, or self-declaration. |
| BR-KYB-01 | For businesses, collect RCCM, NIF, statutes, legal representative identity, and UBO list. |
| BR-TIER-01 | Each account has a unique KYC status: PENDING, VERIFIED, REJECTED, mapped to KYC level 1/2/3. |
| BR-TIER-02 | Transaction attempts above the active KYC threshold are blocked with explicit messaging. |
| BR-AML-01 | Automatic screening against PEP and sanctions lists. |
| BR-AML-02 | Ongoing checking against sanctions and freeze lists (CENAREF, UN, FATF, OFAC) during lifecycle. |
| BR-AML-03 | Automatic AML risk assessment (Low / Medium / High). |
| BR-AML-04 | Confirmed AML match blocks verification and triggers manual review. |

#### Required onboarding screens and fields

| Screen | Fields |
| --- | --- |
| Registration screen | Phone number (MSISDN, RDC format); email; password; OTP code; TOS/CGU acceptance |
| Authentication & security screen | PIN; PIN confirmation; biometrics activation; detected biometric method |
| Customer profile selection screen | Client type; tax residence country |
| Identity document screen (individual) | Document type; document number; issuance and expiry dates; front photo; back photo; liveness selfie |
| Personal information screen | Name; first name; date of birth; place of birth; sex; nationality; address; city/commune/quartier; profession; income source; monthly income estimate |
| Residence proof screen | Proof type; issue date; document file; geolocation if applicable |
| KYB business screen | Company name; RCCM; NIF; legal form; start date; sector; registered address; annual turnover; legal representative; UBO list |

#### Key processing engines

| Engine | Purpose |
| --- | --- |
| OCR extraction engine | Reads and verifies identity document fields and metadata |
| Liveness / facial biometrics engine | Confirms live user presence and document-to-person match |
| AML / PEP / sanctions screening engine | Checks customers against sanctions, PEP, and freeze lists |
| AML risk scoring engine | Assigns risk level and determines review or blocking flow |
| KYC tiering engine | Maps onboarding status to KYC level and transaction threshold rules |

#### Quality controls

| ID | Control |
| --- | --- |
| QC-ONB-01 | Phone format and uniqueness |
| QC-ONB-02 | Valid email format and uniqueness |
| QC-ONB-03 | Minimum age 18 |
| QC-ONB-04 | OCR confidence threshold |
| QC-ONB-05 | Liveness and face match threshold |
| QC-ONB-06 | Identity document uniqueness |
| QC-ONB-07 | Document expiry must be in the future |
| QC-ONB-08 | RCCM/NIF format validation |
| QC-ONB-09 | UBO percentages must be <= 100% |
| QC-ONB-10 | Confirmed AML match blocks verification |

### 4.2 Domain 2 — Core Banking & Multi-currency Accounts (USD/CDF)

#### Business requirements

| ID | Requirement |
| --- | --- |
| BR-CB-01 | Native management of CDF and USD wallet in a single profile. |
| BR-CB-02 | Assignment of a local banking identifier / IBAN for settlements. |
| BR-CB-03 | Support for theme-based sub-accounts and reserve vaults by currency. |
| BR-CB-04 | Real-time account history showing transaction currency and conversion rate. |
| BR-CB-05 | Merchant enrichment in transaction history (merchant name, category, location). |
| BR-CB-06 | Official account statements in PDF and CSV with legal wording. |
| BR-CB-07 | Real-time balance visibility, including available balance calculations. |

#### Required screens and fields

| Screen | Fields |
| --- | --- |
| Accounts screen | Wallet label; currency; book balance; blocked balance; available balance; banking identifier / IBAN |
| Create sub-account screen | Name; currency; savings goal; target amount; initial deposit |
| Transactions history screen | Date range; currency; operation type; merchant/category filter |
| Statement generation screen | Period; export format; currency; language |

#### Core calculation engines

| Engine | Purpose |
| --- | --- |
| Real-time available balance engine | Calculates current liquidity after holds and debits |
| Local IBAN generation engine | Assigns settlement numbers for wallets and sub-accounts |
| Multi-currency valuation engine | Converts balances and transactions across USD and CDF |

#### Quality controls

| ID | Control |
| --- | --- |
| QC-CB-01 | Wallet currency must be USD or CDF |
| QC-CB-02 | Sub-account name length and uniqueness |
| QC-CB-03 | Initial deposit must be within available balance |
| QC-CB-04 | Available balance cannot go negative |
| QC-CB-05 | History entries require unique operation ID, timestamp, and after-balance |
| QC-CB-06 | Statement period cannot exceed 36 months without compliance validation |

### 4.3 Domain 3 — Card Management (Physical & Virtual)

#### Business requirements

| ID | Requirement |
| --- | --- |
| BR-CARD-01 | Physical cards on Visa/Mastercard networks. |
| BR-CARD-02 | Permanent virtual cards generated instantly for online purchases. |
| BR-CARD-03 | Ephemeral virtual cards with dynamic CVC invalidated after use. |
| BR-CARD-04 | Mobile wallet compatibility. |
| BR-CARD-05 | Physical card issuance blocked if account has insufficient balance for baseline provisioning threshold. |
| BR-CARD-06 | Instant card lock/unlock from the app. |
| BR-CARD-07 | Real-time adjustment of ATM and point-of-sale limits by currency and channel. |
| BR-CARD-08 | Real-time activation/deactivation by channel (international payments, web, ATM withdrawals). |

#### Required screens and fields

| Screen | Fields |
| --- | --- |
| Order physical card screen | Card type; currency; embossed name; shipping address; payment method |
| Generate virtual card screen | Currency; virtual card type; linked wallet |
| Manage my card screen | Card status; ATM withdrawal limits; point-of-sale/web limits; international payment toggle; web payment toggle; ATM withdrawal channel toggle |

#### Calculation engines

| Engine | Purpose |
| --- | --- |
| Provisioning threshold validation engine | Verifies account funding before issuing physical cards |
| Card PAN generation engine | Creates valid card identifiers for physical and virtual card products |
| Ephemeral CVC generation engine | Produces dynamic security codes for single-use or short-lived cards |
| Dynamic spending limit engine | Applies channel- and currency-specific usage caps |

#### Quality controls

| ID | Control |
| --- | --- |
| QC-CARD-01 | Card name max 26 chars, no unsupported special characters/diacritics |
| QC-CARD-02 | Physical card order rejected if balance below threshold |
| QC-CARD-03 | Customer-defined limits cannot exceed KYC maximum |
| QC-CARD-04 | Threshold modifications require dual approval |
| QC-CARD-05 | Reuse of an ephemeral CVC is rejected and logged |

### 4.4 Domain 4 — Payment Engine & Interoperability

#### Business requirements

| ID | Requirement |
| --- | --- |
| BR-PAY-01 | Direct interconnection with Mobile Money operators for cash-in / cash-out. |
| BR-PAY-02 | Integration with BCC ATS/RIP for external bank transfers. |
| BR-FX-01 | Instant USD/CDF conversion using official or live market rate with short validity window. |
| BR-FX-02 | Change history and auditability. |
| BR-INTL-01 | International fund sending/receiving via SWIFT and transfer operators. |
| BR-P2P-01 | Instant free transfer between Switsh users by phone, user ID, or QR. |
| BR-P2P-02 | Merchant payment by static or dynamic QR. |
| BR-P2P-03 | Payments by physical or virtual card via NFC; merchant app supports NFC reception. |
| BR-P2P-04 | Bill-splitting across multiple users. |
| BR-P2P-05 | Shared wallets / group cagnotte creation. |

#### Required screens and fields

| Screen | Fields |
| --- | --- |
| P2P transfer screen | Recipient mode; amount; currency; transfer note |
| External bank transfer screen | Beneficiary name; account/IBAN; bank; amount; currency; motive |
| Cash-in / cash-out Mobile Money screen | Operator; MSISDN; amount; direction |
| FX converter screen | Source currency; target currency; amount; display rate; converted amount |
| Merchant payment screen | Amount; currency; merchant ID; payment mode |
| Shared wallet screen | Name; target amount; currency; closing date; contributor list |
| Bill splitting screen | Total amount; participant count; split mode; participant details |

#### Calculation engines

| Engine | Purpose |
| --- | --- |
| Ledger engine for double-entry accounting | Records funds movement in balanced accounting entries |
| FX conversion engine | Calculates and log current conversion rate and resulting amount |
| Transaction anomaly engine | Detects abnormal or suspicious payment patterns |
| Bill splitting engine | Divides a shared charge across participants exactly |

#### Quality controls

| ID | Control |
| --- | --- |
| QC-PAY-01 | Recipient must exist and have an active account |
| QC-PAY-02 | Transfer amount must be > 0, within balance and KYC limit |
| QC-PAY-03 | External IBAN/account must validate with check digit logic |
| QC-PAY-04 | FX rate must be valid within validity window |
| QC-PAY-05 | Splits must total exact invoice amount |
| QC-PAY-06 | Cash-in/out must generate ledger journal entry in real time |

### 4.5 Domain 5 — PFM & Savings

#### Business requirements

| ID | Requirement |
| --- | --- |
| BR-PFM-01 | Automatic expense categorization with consolidated USD/CDF view. |
| BR-PFM-02 | Alerts when monthly budgets are exceeded. |
| BR-PFM-03 | Automatic rounding-up to save the difference to a savings vault. |
| BR-PFM-04 | Recurring transfers to savings sub-accounts. |

#### Required screens and fields

| Screen | Fields |
| --- | --- |
| Define budget screen | Category; monthly limit; currency; alert threshold |
| Automatic savings / rounding screen | Activation; currency; target sub-account |
| Recurring savings transfer screen | Amount; currency; frequency; first execution date; target sub-account |

#### Calculation engines

| Engine | Purpose |
| --- | --- |
| Expense categorization engine | Groups incoming and outgoing transactions into budget categories |
| Automatic rounding engine | Rounds spare change into savings contributions |
| Budget alert engine | Identifies budget overrun and triggers user alerts |

#### Quality controls

| ID | Control |
| --- | --- |
| QC-PFM-01 | Budget limit > 0 and valid currency |
| QC-PFM-02 | Recurring frequency must be valid |
| QC-PFM-03 | Failed recurring transfer triggers notifications and retry policy |
| QC-PFM-04 | Rounding-up must never cause negative available balance |

### 4.6 Domain 6 — Customer Support & In-App Service

#### Business requirements

| ID | Requirement |
| --- | --- |
| BR-SUP-01 | User can access chatbot and FAQ in all active languages. |
| BR-SUP-02 | Secure live chat with support agents. |
| BR-SUP-03 | Open, track, and resolve transaction disputes. |

#### Required screens and fields

| Screen | Fields |
| --- | --- |
| Chatbot and FAQ screen | Selected language; question or FAQ selection |
| Live Chat screen | Contact reason; message; attachment |
| Dispute screen | Transaction reference; dispute reason; description; supporting docs; disputed amount |

#### Calculation engines

| Engine | Purpose |
| --- | --- |
| Conversation routing engine | Routes user questions to chatbot, FAQ, or human agents |
| Dispute workflow engine | Tracks dispute submission, validation, resolution, and closure |

#### Quality controls

| ID | Control |
| --- | --- |
| QC-SUP-01 | Disputed transaction must belong to the customer |
| QC-SUP-02 | Dispute description must be at least 20 characters |
| QC-SUP-03 | SLA assigned automatically with escalation alerts |

### 4.7 Domain 7 — Back-office, Compliance & Infrastructure

#### Business requirements

| ID | Requirement |
| --- | --- |
| BR-BO-01 | Automated detection of anomalous operations and structuring patterns. |
| BR-BO-02 | DS preparation and upload interface for CENAREF. |
| BR-BO-03 | Monitoring of legal cash thresholds and mandatory registration. |
| BR-BO-04 | 360° customer view for compliance staff. |
| BR-BO-05 | Manual validation interface for onboarding dossiers. |
| BR-BO-06 | Immediate account freeze and administrative blocks. |
| BR-BO-07 | Automated regulatory reporting to BCC. |
| BR-BO-08 | Immutable audit trail. |
| BR-BO-09 | Secure hosting with PCI-DSS Level 1 and AES-256 encryption. |
| BR-BO-10 | DevSwitsh supports configurable thresholds without app redeploy. |

#### Required screens and fields

| Screen | Fields |
| --- | --- |
| Threshold configuration screen | Parameter name; current value; proposed value; currency; effective date; justification |
| DS screen | Client; reason; description; supporting files; status |
| 360° customer view | Identity; transaction history; security logs; AML score; KYC archive; consultation history |
| Manual onboarding validation | Dossier; OCR/liveness/AML result; decision; reason |
| Freeze/block screen | Account; reason; requester; scope of block |

#### Calculation engines

| Engine | Purpose |
| --- | --- |
| Unusual transaction detection engine | Flags abnormal operations, structuring, and suspicious activity |
| Legal cash threshold monitoring engine | Tracks threshold breaches and mandatory reporting requirements |
| Regulatory report generator | Prepares reporting packages for BCC and CENAREF |
| Audit trail engine | Preserves immutable evidence for operational and regulatory review |

#### Quality controls

| ID | Control |
| --- | --- |
| QC-BO-01 | Parameter modifications require maker-checker validation |
| QC-BO-02 | New threshold values must be numeric and non-retroactive |
| QC-BO-03 | DS cannot be submitted without file evidence |
| QC-BO-04 | 360° view access is logged |
| QC-BO-05 | Audit log cannot be altered or deleted |
| QC-BO-06 | Freeze/action decisions require documented reason and requestor identity |

## 5. Cross-cutting requirements

### 5.1 Tarification, fees and admin configuration

Business requirements:

| ID | Requirement |
| --- | --- |
| BR-FEE-01 | FX conversion fees are calculated based on rate and/or spread. |
| BR-FEE-02 | FX fees vary by amount bracket and subscription formula. |
| BR-FEE-03 | Merchant acquisition fees are based on transaction amount and MCC volume. |
| BR-FEE-04 | Merchants can view fee details but not tariff configuration. |
| BR-FEE-05 | Intra-Switsh P2P transfer fees vary by subscription plan and amount bracket. |
| BR-FEE-06 | External transfers have separate fee rules by destination and currency. |
| BR-FEE-07 | Each user subscribes to a plan with periodic subscription fee. |
| BR-FEE-08 | Plan changes are calculated on a prorated basis. |
| BR-FEE-09 | Subscription failures trigger retry and escalation policy. |
| BR-FEE-10 | Fee grids are back-office-only and hidden from clients/merchants. |
| BR-FEE-11 | Fees must be displayed transparently before confirmation. |

Back-office screens:

| Screen | Purpose |
| --- | --- |
| Tariff configuration screen | Maintains FX, transfer, and merchant fee rules |
| Subscription plan management screen | Defines plan pricing, limits, and billing behavior |

Quality controls:

| ID | Control |
| --- | --- |
| QC-FEE-01 | Rate or fixed amount >= 0 and within cap |
| QC-FEE-02 | Tariff screen access restricted to IT and Center of Expertise |
| QC-FEE-03 | Fee grid changes require maker-checker approval |
| QC-FEE-04 | Customer must see fee breakdown before confirmation |
| QC-FEE-05 | All tariff changes are logged in audit trail |

### 5.2 Role-based access control (RBAC)

Business requirements:

| ID | Requirement |
| --- | --- |
| BR-RBAC-01 | Six user profiles are defined: Client particulier, Client corporate, IT, KYC profile, Support profile, Centre d'expertise. |
| BR-RBAC-02 | Module-level read/write rights are defined in back-office. |
| BR-RBAC-03 | IT has technical access without direct personal-data access. |
| BR-RBAC-04 | Any RBAC change requires maker-checker validation and audit logging. |

Profiles and minimal permissions:

| Profile | Minimal permissions |
| --- | --- |
| Client particulier | Own account data; cards; transfers; PFM; support |
| Client corporate | Business account; delegated operations |
| IT | Technical configuration; thresholds; tariffs; profiles |
| KYC profile | Onboarding and KYB review |
| Support client | Assistance and dispute handling |
| Centre d'expertise | Arbitration; AML and tariff validation |

Quality controls:

| ID | Control |
| --- | --- |
| QC-RBAC-01 | All unauthorized access attempts are blocked and logged |
| QC-RBAC-02 | Client users cannot access tariff or RBAC admin screens |
| QC-RBAC-03 | RBAC matrix changes require dual approval |
| QC-RBAC-04 | Back-office profiles can only be assigned to back-office users |

### 5.3 Multilingual support and localization

Business requirements:

| ID | Requirement |
| --- | --- |
| BR-LNG-01 | Application available in six languages: French, English, Lingala, Kikongo, Tshiluba, Swahili. |
| BR-LNG-02 | Single language reference repository. |
| BR-LNG-03 | French is default reference language; one default language only. |
| BR-LNG-04 | Legal and financial documents remain legally binding in French. |
| BR-LNG-05 | User chooses language at first launch before registration. |
| BR-LNG-06 | Selected language persists to user profile and channels. |
| BR-LNG-07 | User can change language at any time with no logout. |
| BR-LNG-08 | Sensitive flows must be fully translated. |
| BR-LNG-09 | Language activation/deactivation is managed in back-office without app redeploy. |
| BR-LNG-10 | Deactivation automatically moves affected users to default language. |
| BR-LNG-11 | Activation requires minimum translation coverage threshold. |
| BR-LNG-12 | Language changes require maker-checker and audit trail. |
| BR-LNG-13 | French cannot be deactivated while it is default reference language. |
| BR-LNG-14 | No visible text should be hard-coded in the app. |
| BR-LNG-15 | Localized formatting must preserve the underlying numeric value. |
| BR-LNG-16 | Support channels must route in the user's language. |
| BR-LNG-17 | UTF-8 and long-text support must work without truncation. |
| BR-LNG-18 | Language packs are cached locally for offline operation. |

Screens and fields:

| Screen | Fields |
| --- | --- |
| Language selection at first launch | Available language list; default locale selection |
| Preferences screen for client-side language selection | Current language; save preference; fallback language |
| Back-office language management screen | Language status; default language; coverage threshold; activation/deactivation |
| Translation management screen | String keys; translation values; review and publish state |

Quality controls:

| ID | Control |
| --- | --- |
| QC-LNG-01 | Language codes must belong to valid set {fr, en, ln, kg, lua, sw} |
| QC-LNG-02 | Activation rejects incomplete coverage |
| QC-LNG-03 | Only one default language, French cannot be disabled if default |
| QC-LNG-04 | Deactivation triggers automatic fallback and notification |
| QC-LNG-05 | Translation changes require maker-checker |
| QC-LNG-06 | No hard-coded visible text |
| QC-LNG-07 | Only published translations are shown |
| QC-LNG-08 | Numeric values remain unchanged across locale formatting |
| QC-LNG-09 | Text expansion tested up to +40% |
| QC-LNG-10 | Admin access is restricted to IT and Center of Expertise |
| QC-LNG-11 | Support ticket language must follow user language |

## 6. Non-functional requirements

| Category | Requirement |
| --- | --- |
| Performance | AML screening under 3s |
| Performance | NFC transaction under 3s |
| Performance | Push notification under 2s |
| Performance | Available balance calculation under 500ms |
| Availability | Real-time operations |
| Availability | High availability |
| Availability | Offline-first design in low-connectivity areas |
| Security | TLS 1.3, AES-256, PCI-DSS Level 1 |
| Security | Argon2 / BCrypt for PIN hashing |
| Security | SCA required for sensitive actions |
| Compliance | Continuous AML/PEP screening |
| Compliance | KYC tiering-based limits |
| Compliance | Immutable audit trail |
| Compliance | BCC reporting |
| Accounting | Double-entry ledger, atomic and idempotent |
| Accounting | Daily bank reconciliation |
| Data quality | Format, uniqueness, and consistency validation at input and in background jobs |
| Scalability | Support up to 40,000 active users and 1,000 merchants |
| Multilingual support | French, English, Lingala, Kikongo, Tshiluba, Swahili across UI, notifications, documents, and support channels |

## 7. Transition requirements

| ID | Requirement |
| --- | --- |
| TR-01 | Finalize Switsh SARL and secure a BCC intention letter before commercial launch. |
| TR-02 | Certify hosting and payment flows according to PCI-DSS Level 1 before physical card launch. |
| TR-03 | Launch a field pilot in Kinshasa with real merchants using QR/NFC before generalization. |
| TR-04 | Train support and compliance teams before public opening. |
| TR-05 | Integrate Mobile Money and international transfer partners before full launch. |

## 8. Risks and dependencies

| ID | Risk / dependency |
| --- | --- |
| RSK-01 | Delayed BCC banking partnership affecting payment operations |
| RSK-02 | AML/KYC non-compliance |
| RSK-03 | Identity fraud via fake documents or SIM mismatch |
| RSK-04 | Third-party card issuer delays |
| RSK-05 | Mobile Money or international aggregator unavailability |
| RSK-06 | Poor connectivity in semi-urban areas |
| RSK-07 | Cash threshold and CENAREF reporting failures |
| RSK-08 | Onboarding data quality issues |
| RSK-09 | Incomplete translations in local languages |

## 9. Key assumptions and constraints

### Assumptions

| ID | Assumption |
| --- | --- |
| AS-01 | Third-party providers remain available and integrable via API. |
| AS-02 | BCC regulatory framework remains stable during rollout. |
| AS-03 | RDC identity documents remain OCR-verifiable. |
| AS-04 | ARPTC provides necessary SIM ownership data. |

### Constraints

| ID | Constraint |
| --- | --- |
| CT-01 | Physical card issuance requires at least 5 USD provisioning, configurable in back-office. |
| CT-02 | Connectivity may be weak in some corridor areas; offline-first design is required. |
| CT-03 | PCI-DSS Level 1 and AES-256 encryption are mandatory. |
| CT-04 | Multilingual support is mandatory across support and user channels. |

## 10. Traceability summary

| Domain | Requirements | Controls | Main stakeholders |
| --- | --- | --- | --- |
| Onboarding & KYC/AML | BR-ONB-01 to BR-AML-04 | QC-ONB-01 to QC-ONB-10 | Client, compliance, BCC, CENAREF, ARPTC |
| Core Banking | BR-CB-01 to BR-CB-07 | QC-CB-01 to QC-CB-06 | Client, BCC |
| Cards | BR-CARD-01 to BR-CARD-08 | QC-CARD-01 to QC-CARD-05 | Client, card issuers |
| Payments & interoperability | BR-PAY-01, BR-FX-01/02, BR-INTL-01, BR-P2P-01 to BR-P2P-05 | QC-PAY-01 to QC-PAY-06 | Client, merchants, Mobile Money operators, BCC |
| PFM & savings | BR-PFM-01 to BR-PFM-04 | QC-PFM-01 to QC-PFM-04 | Client |
| Support | BR-SUP-01 to BR-SUP-03 | QC-SUP-01 to QC-SUP-03 | Client, support |
| Back-office & compliance | BR-BO-01 to BR-BO-10 | QC-BO-01 to QC-BO-06 | Compliance, CENAREF, BCC |
| Tarification & fees | BR-FEE-01 to BR-FEE-11 | QC-FEE-01 to QC-FEE-05 | IT, Center of Expertise |
| RBAC | BR-RBAC-01 to BR-RBAC-04 | QC-RBAC-01 to QC-RBAC-04 | IT, Center of Expertise |
| Multi-language localization | BR-LNG-01 to BR-LNG-18 | QC-LNG-01 to QC-LNG-11 | Client, IT, support |

## 11. Glossary

| Term | Definition |
| --- | --- |
| BR | Business Requirement |
| QC | Quality Control |
| KYC | Know Your Customer |
| KYB | Know Your Business |
| AML/LBC-FT | Anti-Money Laundering / Fight against money laundering and terrorist financing |
| CENAREF | National Financial Intelligence Unit of the DRC |
| BCC | Central Bank of the Congo |
| ARPTC | Regulatory authority for telecommunications |
| SCA | Strong Customer Authentication |
| PEP | Politically Exposed Person |
| UBO | Ultimate Beneficial Owner |
| NFT / not applicable here | No extra glossary items needed beyond the original context |
| Ledger | Double-entry accounting ledger |
| ATS/RIP | BCC interbank settlement systems |
| NFC | Near Field Communication |
| API | Application Programming Interface |
| Maker-checker | Dual approval workflow before sensitive changes are applied |
| DevSwitsh | Internal admin/back-office platform for operational configuration |
| RBAC | Role-Based Access Control |
| i18n / l10n | Internationalization / localization |

## 12. Final statement

The Switsh platform is a regulated mobile financial ecosystem designed for the DRC, combining onboarding, banking, cards, payments, PFM, support, compliance, and multilingual operations under a strong governance model. The requirements captured in this specification define the product intent, core business rules, operational controls, and regulatory obligations required to deliver a compliant and scalable solution.
