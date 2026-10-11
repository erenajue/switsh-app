package com.switsh.app.domains.onboarding

data class DocumentView(
    val documentId: String? = null,
    val attemptId: String? = null,
    val documentType: DocumentType? = null,
    val status: VerificationStatus? = null,
    val state: ApplicationState? = null,
)
