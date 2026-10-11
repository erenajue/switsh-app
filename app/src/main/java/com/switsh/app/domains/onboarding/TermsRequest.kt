package com.switsh.app.domains.onboarding

// Dates are ISO-8601 strings (date: yyyy-MM-dd, date-time: RFC 3339).
data class TermsRequest(
    val termsVersion: String,
    val privacyPolicyVersion: String,
    val accepted: Boolean,
)
