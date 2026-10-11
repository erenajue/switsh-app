package com.switsh.app.domains.onboarding

data class ContactStatusView(
    val phone: VerificationStatus? = null,
    val email: VerificationStatus? = null,
)
