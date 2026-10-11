package com.switsh.app.domains.onboarding

data class ChallengeView(
    val applicationId: String? = null,
    val contactType: ContactType? = null,
    val expiresAt: String? = null,
    val maxAttempts: Int? = null,
    val resendAllowedAt: String? = null,
    val delivery: String? = null,
)
