package com.switsh.clients

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
}
