package com.switsh.app.domains.onboarding

/** Optional server-reported AML outcomes; missing fields must never be inferred by the client. */
data class AmlAssessmentView(
    val screeningStatus: String? = null,
    val ongoingStatus: String? = null,
    val riskLevel: String? = null,
    val decision: String? = null,
)
