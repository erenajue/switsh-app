package com.switsh.app.domains.onboarding

/** Exactly one of [code] (SMS OTP) or [token] (email link) must be supplied. */
data class VerifyContactRequest(val code: String? = null, val token: String? = null) {
    init {
        require((code == null) != (token == null)) { "Exactly one of code or token must be supplied" }
    }
}
