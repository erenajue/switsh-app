package com.switsh.app.domains.onboarding

import com.google.gson.annotations.SerializedName

data class CreateOnboardingRequest(@SerializedName("client_id") val clientId: String? = null)
