package com.switsh.app.model

import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PinHasherTest {
    @Test
    fun bcryptHashVerifiesOnlyTheCorrectPin() {
        val hash = PinHasher.hash("4827")

        assertTrue(hash.startsWith("\$2"))
        assertTrue(PinHasher.verify("4827", hash, null).valid)
        assertFalse(PinHasher.verify("4828", hash, null).valid)
    }

    @Test
    fun successfulLegacyVerificationReturnsBcryptUpgrade() {
        val pin = "4827"
        val salt = ByteArray(16) { it.toByte() }
        val legacyHash = Base64.getEncoder().encodeToString(
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(PBEKeySpec(pin.toCharArray(), salt, LEGACY_ITERATIONS, HASH_BITS))
                .encoded,
        )
        val encodedSalt = Base64.getEncoder().encodeToString(salt)

        val verified = PinHasher.verify(pin, legacyHash, encodedSalt)

        assertTrue(verified.valid)
        assertNotNull(verified.upgradedHash)
        assertTrue(PinHasher.verify(pin, verified.upgradedHash!!, null).valid)
    }

    @Test
    fun incorrectLegacyPinDoesNotReturnAnUpgrade() {
        val salt = ByteArray(16) { it.toByte() }
        val pin = "4827"
        val legacyHash = Base64.getEncoder().encodeToString(
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(PBEKeySpec(pin.toCharArray(), salt, LEGACY_ITERATIONS, HASH_BITS))
                .encoded,
        )
        val result = PinHasher.verify(
            "4828",
            legacyHash,
            Base64.getEncoder().encodeToString(salt),
        )

        assertFalse(result.valid)
        assertNull(result.upgradedHash)
    }

    private companion object {
        const val LEGACY_ITERATIONS = 120_000
        const val HASH_BITS = 256
    }
}
