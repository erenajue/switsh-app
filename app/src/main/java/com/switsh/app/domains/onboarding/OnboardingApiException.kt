package com.switsh.app.domains.onboarding

class OnboardingApiException(
    val httpStatus: Int,
    val problem: Problem? = null,
    val legacyError: String? = null,
    message: String? = null,
) : RuntimeException(message ?: problem?.title ?: legacyError ?: "HTTP $httpStatus")
