package com.switsh.app.model

import java.security.MessageDigest
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import org.mindrot.jbcrypt.BCrypt

internal object PinHasher {
    data class Verification(val valid: Boolean, val upgradedHash: String? = null)

    fun hash(pin: String): String = BCrypt.hashpw(pin, BCrypt.gensalt(BCRYPT_COST))

    fun verify(pin: String, storedHash: String, legacySalt: String?): Verification {
        if (storedHash.startsWith(BCRYPT_PREFIX)) {
            val valid = runCatching { BCrypt.checkpw(pin, storedHash) }.getOrDefault(false)
            return Verification(valid)
        }

        val salt = legacySalt?.let { runCatching { Base64.getDecoder().decode(it) }.getOrNull() }
            ?: return Verification(false)
        val expected = runCatching { Base64.getDecoder().decode(storedHash) }.getOrNull()
            ?: return Verification(false)
        val valid = MessageDigest.isEqual(expected, hashLegacyPin(pin, salt))
        return Verification(valid, if (valid) hash(pin) else null)
    }

    private fun hashLegacyPin(pin: String, salt: ByteArray): ByteArray =
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(PBEKeySpec(pin.toCharArray(), salt, LEGACY_ITERATIONS, HASH_BITS))
            .encoded

    private const val BCRYPT_COST = 12
    private const val BCRYPT_PREFIX = "\$2"
    private const val LEGACY_ITERATIONS = 120_000
    private const val HASH_BITS = 256
}
