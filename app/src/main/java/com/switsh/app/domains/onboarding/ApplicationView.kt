package com.switsh.app.domains.onboarding

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
