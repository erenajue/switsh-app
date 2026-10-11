package com.switsh.app.domains.onboarding

data class ContactResultView(
    val applicationId: String? = null,
    val contactType: ContactType? = null,
    val status: VerificationStatus? = null,
    val attemptsRemaining: Int? = null,
    val contacts: ContactStatusView? = null,
    val state: ApplicationState? = null,
)
