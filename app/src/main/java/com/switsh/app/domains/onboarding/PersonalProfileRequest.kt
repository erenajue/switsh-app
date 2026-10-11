package com.switsh.app.domains.onboarding

data class PersonalProfileRequest(
    val surname: String,
    val givenNames: String,
    val dateOfBirth: String,
    val placeOfBirth: String,
    val sex: Sex,
    val nationality: String,
    val address: String,
    val cityOrCommune: String,
    val occupation: String,
    val sourceOfFunds: SourceOfFunds,
    val incomeBand: IncomeBand? = null,
)
