package com.switsh.app.domains.onboarding

data class ProfileView(
    val applicationId: String? = null,
    val state: ApplicationState? = null,
    val complete: Boolean? = null,
    val updatedAt: String? = null,
)
