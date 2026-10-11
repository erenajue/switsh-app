package com.switsh.app.domains.onboarding

data class UploadSessionView(
    val uploadSessionId: String? = null,
    val objectKey: String? = null,
    val uploadUrl: String? = null,
    val maxBytes: Long? = null,
    val allowedMimeTypes: List<String>? = null,
    val expiresAt: String? = null,
)
