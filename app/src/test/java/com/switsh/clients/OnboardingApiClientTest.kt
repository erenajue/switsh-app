package com.switsh.clients

import com.switsh.app.domains.onboarding.*
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class OnboardingApiClientTest {
    private val json = headersOf(HttpHeaders.ContentType, "application/json")
    private val problem = headersOf(HttpHeaders.ContentType, "application/problem+json")

    @Test
    fun writeSendsAuthIdempotencyAndCorrelation() = runTest {
        var seen: HttpRequestData? = null
        val engine = MockEngine { req ->
            seen = req
            respond("""{"applicationId":"a1","state":"DRAFT"}""", HttpStatusCode.Created, json)
        }
        val client = OnboardingApiClient("https://api.example.com", { "tok" }, engine)
        val res = client.registerApplication(
            RegisterRequest("+243999999999", "a@b.c", "fr-CD", TermsRequest("1", "1", true)),
        )
        assertEquals(ApplicationState.DRAFT, res.state)
        assertEquals("https://api.example.com/v1/applications", seen!!.url.toString())
        assertEquals("Bearer tok", seen!!.headers[HttpHeaders.Authorization])
        assertNotNull(seen!!.headers["Idempotency-Key"])
        assertNotNull(seen!!.headers["X-Correlation-Id"])
    }

    @Test
    fun problemResponseBecomesException() = runTest {
        val engine = MockEngine {
            respond("""{"title":"nope","status":404,"code":"NOT_FOUND"}""", HttpStatusCode.NotFound, problem)
        }
        val client = OnboardingApiClient("https://api.example.com", { "tok" }, engine)
        try {
            client.getApplication("x"); fail()
        } catch (e: OnboardingApiException) {
            assertEquals(404, e.httpStatus)
            assertEquals("NOT_FOUND", e.problem?.code)
        }
    }

    @Test
    fun legacyErrorParsed() = runTest {
        val engine = MockEngine { respond("""{"error":"bad"}""", HttpStatusCode.BadRequest, json) }
        val client = OnboardingApiClient("https://api.example.com", { "tok" }, engine)
        try {
            client.completeOnboardingSection("1", "coordonnees", mapOf("a" to 1)); fail()
        } catch (e: OnboardingApiException) {
            assertEquals("bad", e.legacyError)
        }
    }

    @Test
    fun rejectsCleartextRemoteHost() {
        try {
            OnboardingApiClient("http://api.example.com", { "t" }); fail()
        } catch (e: IllegalArgumentException) {
            assertTrue(true)
        }
    }

    @Test
    fun allowsCleartextRemoteHostWhenExplicitlyEnabled() {
        OnboardingApiClient("http://192.168.1.25:8080", { "t" }, allowInsecureHttp = true)
    }

    @Test
    fun uploadDocumentResolvesMemorySchemeToBaseUrl() = runTest {
        var seen: HttpRequestData? = null
        val engine = MockEngine { req ->
            seen = req
            respond("", HttpStatusCode.OK)
        }
        val client = OnboardingApiClient("http://localhost:8080", { "tok" }, engine, allowInsecureHttp = true)
        val session = UploadSessionView(uploadUrl = "memory://upload/session123")
        client.uploadDocument(session, byteArrayOf(1, 2, 3), "image/jpeg")
        assertEquals("http://localhost:8080/upload/session123", seen!!.url.toString())
        assertEquals("Bearer tok", seen!!.headers[HttpHeaders.Authorization])
    }

    @Test
    fun uploadDocumentPreservesExternalHttpsUrlWithoutAuthHeader() = runTest {
        var seen: HttpRequestData? = null
        val engine = MockEngine { req ->
            seen = req
            respond("", HttpStatusCode.OK)
        }
        val client = OnboardingApiClient("http://localhost:8080", { "tok" }, engine, allowInsecureHttp = true)
        val session = UploadSessionView(uploadUrl = "https://s3.amazonaws.com/bucket/file.jpg")
        client.uploadDocument(session, byteArrayOf(1, 2, 3), "image/jpeg")
        assertEquals("https://s3.amazonaws.com/bucket/file.jpg", seen!!.url.toString())
        assertTrue(seen!!.headers[HttpHeaders.Authorization] == null)
    }
}
