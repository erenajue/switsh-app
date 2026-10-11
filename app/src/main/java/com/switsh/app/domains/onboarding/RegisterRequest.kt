package com.switsh.app.domains.onboarding

data class RegisterRequest(
    val phoneNumber: String,
    val email: String,
    val locale: String,
    val terms: TermsRequest,
    val credentialReference: String? = null,
)
