package com.switsh.app.domains.onboarding

import com.google.gson.annotations.SerializedName

// Legacy wizard model: camelCase properties mapped to snake_case wire names.
data class OnboardingRecord(
    val id: String? = null,
    @SerializedName("client_id") val clientId: String? = null,
    val status: String? = null,
    @SerializedName("completed_sections") val completedSections: List<String>? = null,
)
