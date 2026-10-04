package com.switsh.clients

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.gson.gson
import java.net.URI
import java.util.UUID

/**
 * Ktor client for the Onboarding & KYC/AML API (onboarding-api.yml).
 *
 * Security:
 *  - every call carries `Authorization: Bearer <token>`; [tokenProvider] is invoked for each
 *    request because tokens are short-lived (<= 7 min) and must never be cached by the client;
 *  - cleartext HTTP is only accepted for loopback hosts (local development);
 *  - every write (POST/PUT/PATCH/DELETE) sends `X-Correlation-Id`, and the application
 *    endpoints also send the mandatory `Idempotency-Key`;
 *  - 10 s connect/request/socket timeouts.
 */
class OnboardingApiClient(
    baseUrl: String,
    private val tokenProvider: suspend () -> String,
    engine: HttpClientEngine? = null,
    private val correlationIdProvider: () -> String = { UUID.randomUUID().toString() },
) : AutoCloseable {

    private val base = baseUrl.trimEnd('/')
    private val gson = Gson()

    init {
        val uri = URI(base)
        val loopback = uri.host in setOf("localhost", "127.0.0.1", "10.0.2.2", "::1", "[::1]")
        require(uri.scheme == "https" || (uri.scheme == "http" && loopback)) {
            "Onboarding API requires HTTPS (HTTP allowed for loopback only)"
        }
    }

    private val configure: HttpClientConfig<*>.() -> Unit = {
        expectSuccess = false
        install(ContentNegotiation) { gson() }
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MS
            connectTimeoutMillis = TIMEOUT_MS
            socketTimeoutMillis = TIMEOUT_MS
        }
        defaultRequest { header(HttpHeaders.Accept, "application/json, application/problem+json") }
    }

    private val client: HttpClient =
        if (engine != null) HttpClient(engine, configure) else HttpClient(OkHttp, configure)

    // ---- Applications ----

    suspend fun registerApplication(
        request: RegisterRequest,
        idempotencyKey: String = newKey(),
        correlationId: String = correlationIdProvider(),
    ): ApplicationView = send(Kind.PROBLEM) { token ->
        client.post("$base/v1/applications") { write(token, idempotencyKey, correlationId); json(request) }
    }

    suspend fun getApplication(
        applicationId: String,
        correlationId: String = correlationIdProvider(),
    ): ApplicationView = send(Kind.PROBLEM) { token ->
        client.get("$base/v1/applications/${seg(applicationId)}") { read(token, correlationId) }
    }

    suspend fun issueContactChallenge(
        applicationId: String,
        contactType: ContactType,
        idempotencyKey: String = newKey(),
        correlationId: String = correlationIdProvider(),
    ): ChallengeView = send(Kind.PROBLEM) { token ->
        client.post("$base/v1/applications/${seg(applicationId)}/contacts/$contactType/challenges") {
            write(token, idempotencyKey, correlationId)
        }
    }

    suspend fun verifyContact(
        applicationId: String,
        contactType: ContactType,
        request: VerifyContactRequest,
        idempotencyKey: String = newKey(),
        correlationId: String = correlationIdProvider(),
    ): ContactResultView = send(Kind.PROBLEM) { token ->
        client.post("$base/v1/applications/${seg(applicationId)}/contacts/$contactType/verify") {
            write(token, idempotencyKey, correlationId); json(request)
        }
    }

    suspend fun selectProfile(
        applicationId: String,
        request: ProfileSelectionRequest,
        idempotencyKey: String = newKey(),
        correlationId: String = correlationIdProvider(),
    ): ApplicationView = send(Kind.PROBLEM) { token ->
        client.put("$base/v1/applications/${seg(applicationId)}/profile-selection") {
            write(token, idempotencyKey, correlationId); json(request)
        }
    }

    suspend fun createUploadSession(
        applicationId: String,
        request: UploadSessionRequest,
        idempotencyKey: String = newKey(),
        correlationId: String = correlationIdProvider(),
    ): UploadSessionView = send(Kind.PROBLEM) { token ->
        client.post("$base/v1/applications/${seg(applicationId)}/documents/upload-sessions") {
            write(token, idempotencyKey, correlationId); json(request)
        }
    }

    suspend fun attachIdentityDocument(
        applicationId: String,
        request: IdentityDocumentRequest,
        idempotencyKey: String = newKey(),
        correlationId: String = correlationIdProvider(),
    ): DocumentView = send(Kind.PROBLEM) { token ->
        client.post("$base/v1/applications/${seg(applicationId)}/identity-documents") {
            write(token, idempotencyKey, correlationId); json(request)
        }
    }

    suspend fun savePersonalProfile(
        applicationId: String,
        request: PersonalProfileRequest,
        idempotencyKey: String = newKey(),
        correlationId: String = correlationIdProvider(),
    ): ProfileView = send(Kind.PROBLEM) { token ->
        client.put("$base/v1/applications/${seg(applicationId)}/personal-profile") {
            write(token, idempotencyKey, correlationId); json(request)
        }
    }

    suspend fun withdrawApplication(
        applicationId: String,
        idempotencyKey: String = newKey(),
        correlationId: String = correlationIdProvider(),
    ): ApplicationView = send(Kind.PROBLEM) { token ->
        client.post("$base/v1/applications/${seg(applicationId)}/withdraw") {
            write(token, idempotencyKey, correlationId)
        }
    }

    suspend fun submitApplication(
        applicationId: String,
        idempotencyKey: String = newKey(),
        correlationId: String = correlationIdProvider(),
    ): ApplicationView = send(Kind.PROBLEM) { token ->
        client.post("$base/v1/applications/${seg(applicationId)}/submit") {
            write(token, idempotencyKey, correlationId)
        }
    }

    // ---- Legacy onboarding wizard ----

    suspend fun createOnboarding(
        request: CreateOnboardingRequest? = null,
        correlationId: String = correlationIdProvider(),
    ): OnboardingRecord = send(Kind.LEGACY) { token ->
        client.post("$base/v1/onboardings") {
            write(token, null, correlationId)
            if (request != null) json(request)
        }
    }

    suspend fun getOnboarding(
        id: String,
        correlationId: String = correlationIdProvider(),
    ): OnboardingRecord = send(Kind.LEGACY) { token ->
        client.get("$base/v1/onboardings/${seg(id)}") { read(token, correlationId) }
    }

    suspend fun getOnboardingVerificationStatus(
        id: String,
        correlationId: String = correlationIdProvider(),
    ): OnboardingVerificationStatus = send(Kind.LEGACY) { token ->
        client.get("$base/v1/onboardings/${seg(id)}/verification-status") { read(token, correlationId) }
    }

    /** [sectionSlug]: inscription, informations-personnelles, coordonnees, informations-entreprise,
     * documents-legaux, informations-mobile-money, representant-legal, validation, statut-de-verification. */
    suspend fun completeOnboardingSection(
        id: String,
        sectionSlug: String,
        data: Map<String, Any?>,
        correlationId: String = correlationIdProvider(),
    ): OnboardingRecord = send(Kind.LEGACY) { token ->
        client.patch("$base/v1/onboardings/${seg(id)}/sections/${seg(sectionSlug)}") {
            write(token, null, correlationId); json(data)
        }
    }

    override fun close() = client.close()

    // ---- internals ----

    private enum class Kind { PROBLEM, LEGACY }

    private fun HttpRequestBuilder.read(token: String, correlationId: String) {
        header(HttpHeaders.Authorization, "Bearer $token")
        header(CORRELATION_ID, correlationId)
    }

    private fun HttpRequestBuilder.write(token: String, idempotencyKey: String?, correlationId: String) {
        header(HttpHeaders.Authorization, "Bearer $token")
        header(CORRELATION_ID, correlationId)
        if (idempotencyKey != null) header(IDEMPOTENCY_KEY, idempotencyKey)
    }

    private fun HttpRequestBuilder.json(body: Any) {
        contentType(ContentType.Application.Json)
        setBody(body)
    }

    private suspend inline fun <reified T> send(kind: Kind, call: suspend (String) -> HttpResponse): T {
        val response = call(tokenProvider())
        if (response.status.isSuccess()) return response.body()
        val text = runCatching { response.bodyAsText() }.getOrDefault("")
        val status = response.status.value
        throw when (kind) {
            Kind.PROBLEM -> OnboardingApiException(
                status, problem = runCatching { gson.fromJson(text, Problem::class.java) }.getOrNull(),
            )
            Kind.LEGACY -> OnboardingApiException(
                status,
                legacyError = runCatching {
                    gson.fromJson(text, LegacyErrorBody::class.java)?.error
                }.getOrNull(),
            )
        }
    }

    private fun seg(value: String) = java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20")
    private fun newKey() = UUID.randomUUID().toString()

    private data class LegacyErrorBody(val error: String? = null)

    private companion object {
        const val TIMEOUT_MS = 10_000L
        const val CORRELATION_ID = "X-Correlation-Id"
        const val IDEMPOTENCY_KEY = "Idempotency-Key"
    }
}
