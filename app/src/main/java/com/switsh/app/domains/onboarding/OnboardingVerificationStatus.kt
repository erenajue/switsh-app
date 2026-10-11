package com.switsh.app.domains.onboarding

import com.google.gson.annotations.SerializedName

data class OnboardingVerificationStatus(
    val id: String? = null,
    val status: String? = null,
    @SerializedName("completed_sections") val completedSections: List<String>? = null,
    @SerializedName("verification_completed") val verificationCompleted: Boolean? = null,
)
