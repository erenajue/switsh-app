package com.switsh.app.domains.onboarding

data class IdentityDocumentRequest(
    val documentType: DocumentType,
    val documentNumber: String,
    val issuingCountry: String,
    val issueDate: String,
    val expiryDate: String,
    val frontUploadId: String,
    val backUploadId: String? = null,
    val selfieUploadId: String? = null,
)
