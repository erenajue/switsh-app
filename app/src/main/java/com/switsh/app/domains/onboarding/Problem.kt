package com.switsh.app.domains.onboarding

/** RFC 9457 problem document. */
data class Problem(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val code: String? = null,
    val correlationId: String? = null,
    val errors: List<FieldError>? = null,
)
