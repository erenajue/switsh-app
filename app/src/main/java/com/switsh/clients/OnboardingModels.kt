package com.switsh.clients

import com.google.gson.annotations.SerializedName

enum class ContactType { PHONE, EMAIL }
enum class CustomerType { INDIVIDUAL, BUSINESS }
enum class KycTier { TIER_1, TIER_2, TIER_3 }
enum class DocumentType { CENI, ONIP_NIN, BIOMETRIC_PASSPORT, RESIDENCE_PERMIT }
enum class Sex { F, M, X }
enum class SourceOfFunds { SALARY, BUSINESS_INCOME, SAVINGS, REMITTANCE, PENSION, OTHER }
enum class IncomeBand { BAND_0_200K, BAND_200K_1M, BAND_1M_5M, BAND_5M_PLUS }

enum class VerificationStatus {
    NOT_STARTED, PENDING, PROCESSING, PASSED, FAILED, REVIEW_REQUIRED, EXPIRED, CANCELLED
}

enum class ComplianceStatus {
    PENDING, REVIEW_REQUIRED, VERIFIED, SUSPENDED, REJECTED, EXPIRED, WITHDRAWN
}

enum class ApplicationState {
    DRAFT, CONTACT_PENDING, CONTACT_VERIFIED, PROFILE_SELECTED, EVIDENCE_IN_PROGRESS,
    AUTOMATED_CHECKS, MANUAL_REVIEW, INFORMATION_REQUESTED, APPROVED, REASSESSMENT_REQUIRED,
    SUSPENDED, REJECTED, EXPIRED, WITHDRAWN
}

// Dates are ISO-8601 strings (date: yyyy-MM-dd, date-time: RFC 3339).

data class TermsRequest(
    val termsVersion: String,
    val privacyPolicyVersion: String,
    val accepted: Boolean,
)

data class RegisterRequest(
    val phoneNumber: String,
    val email: String,
    val locale: String,
    val terms: TermsRequest,
    val credentialReference: String? = null,
)

/** Exactly one of [code] (SMS OTP) or [token] (email link) must be supplied. */
data class VerifyContactRequest(val code: String? = null, val token: String? = null) {
    init {
        require((code == null) != (token == null)) { "Exactly one of code or token must be supplied" }
    }
}

data class ProfileSelectionRequest(val customerType: CustomerType, val taxResidence: String)

data class UploadSessionRequest(val purpose: String, val contentType: String)

data class IdentityDocumentRequest(
    val documentType: DocumentType,
    val documentNumber: String,
    val issuingCountry: String,
    val issueDate: String,
    val expiryDate: String,
    val frontUploadId: String,
    val backUploadId: String? = null,
    val selfieUploadId: String? = null,
)

data class PersonalProfileRequest(
    val surname: String,
    val givenNames: String,
    val dateOfBirth: String,
    val placeOfBirth: String,
    val sex: Sex,
    val nationality: String,
    val address: String,
    val cityOrCommune: String,
    val occupation: String,
    val sourceOfFunds: SourceOfFunds,
    val incomeBand: IncomeBand? = null,
)

data class NextAction(val code: String? = null, val message: String? = null)

data class ContactStatusView(
    val phone: VerificationStatus? = null,
    val email: VerificationStatus? = null,
)

data class ApplicationView(
    val applicationId: String? = null,
    val version: Long? = null,
    val state: ApplicationState? = null,
    val complianceStatus: ComplianceStatus? = null,
    val customerType: CustomerType? = null,
    val taxResidence: String? = null,
    val kycTier: KycTier? = null,
    val contacts: ContactStatusView? = null,
    val nextActions: List<NextAction>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class ChallengeView(
    val applicationId: String? = null,
    val contactType: ContactType? = null,
    val expiresAt: String? = null,
    val maxAttempts: Int? = null,
    val resendAllowedAt: String? = null,
    val delivery: String? = null,
)

data class ContactResultView(
    val applicationId: String? = null,
    val contactType: ContactType? = null,
    val status: VerificationStatus? = null,
    val attemptsRemaining: Int? = null,
    val contacts: ContactStatusView? = null,
    val state: ApplicationState? = null,
)

data class UploadSessionView(
    val uploadSessionId: String? = null,
    val objectKey: String? = null,
    val uploadUrl: String? = null,
    val maxBytes: Long? = null,
    val allowedMimeTypes: List<String>? = null,
    val expiresAt: String? = null,
)

data class DocumentView(
    val documentId: String? = null,
    val attemptId: String? = null,
    val documentType: DocumentType? = null,
    val status: VerificationStatus? = null,
    val state: ApplicationState? = null,
)

data class ProfileView(
    val applicationId: String? = null,
    val state: ApplicationState? = null,
    val complete: Boolean? = null,
    val updatedAt: String? = null,
)

// Legacy wizard: properties are camelCase, mapped to the snake_case wire names

data class CreateOnboardingRequest(@SerializedName("client_id") val clientId: String? = null)

data class OnboardingRecord(
    val id: String? = null,
    @SerializedName("client_id") val clientId: String? = null,
    val status: String? = null,
    @SerializedName("completed_sections") val completedSections: List<String>? = null,
)

data class OnboardingVerificationStatus(
    val id: String? = null,
    val status: String? = null,
    @SerializedName("completed_sections") val completedSections: List<String>? = null,
    @SerializedName("verification_completed") val verificationCompleted: Boolean? = null,
)

// Errors

data class FieldError(val field: String? = null, val code: String? = null)

/** RFC 9457 problem document. */
data class Problem(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val code: String? = null,
    val correlationId: String? = null,
    val errors: List<FieldError>? = null,
)

class OnboardingApiException(
    val httpStatus: Int,
    val problem: Problem? = null,
    val legacyError: String? = null,
    message: String? = null,
) : RuntimeException(message ?: problem?.title ?: legacyError ?: "HTTP $httpStatus")
