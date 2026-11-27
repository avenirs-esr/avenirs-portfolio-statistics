package fr.avenirsesr.portfolio.statistics.event.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.repository.StatisticEventJpaRepository;
import fr.avenirsesr.portfolio.statistics.shared.infrastructure.ContainerConfigurationTest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// Requests go through HTTP and commit on the server side: the test transaction (rollback) of the
// base class would not clean them.
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class StatisticEventControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/statistics/events";
  private static final String ADMIN_TOKEN_HEADER = "X-ADMIN-TOKEN";
  private static final String COUNT_PATH = BASE_PATH + "/count";
  private static final String UNKNOWN_EVENT_TYPE = "UNKNOWN_EVENT_TYPE";
  private static final UUID INSTITUTION_ID = UUID.randomUUID();

  @Autowired private WebTestClient webTestClient;
  @Autowired private StatisticEventJpaRepository statisticEventJpaRepository;

  @Value("${security.authentication.api-key}")
  private String apiKey;

  @Value("${admin.token}")
  private String adminToken;

  @Value("${user.super-admin.payload}")
  private String superAdminPayload;

  @Value("${user.super-admin.signature}")
  private String superAdminSignature;

  @Value("${user.staff.payload}")
  private String staffPayload;

  @Value("${user.staff.signature}")
  private String staffSignature;

  @BeforeEach
  void setup() {
    statisticEventJpaRepository.deleteAll();
  }

  private static String eventBody(UUID eventId, String occurredAt) {
    return eventBody(eventId, "TRACE_CREATED", occurredAt);
  }

  private static String eventBody(UUID eventId, String eventType, String occurredAt) {
    return """
    {
      %s
      "eventType": "%s",
      "userId": "%s",
      "userCategory": "STUDENT",
      "occurredAt": "%s",
      "institutionId": "%s",
      "objectType": "TRACE",
      "objectId": "%s",
      "metadata": {"traceType": "FILE"}
    }
    """
        .formatted(
            eventId == null ? "" : "\"eventId\": \"" + eventId + "\",",
            eventType,
            UUID.randomUUID(),
            occurredAt,
            INSTITUTION_ID,
            UUID.randomUUID());
  }

  private WebTestClient.ResponseSpec postWithApiKey(String body) {
    return webTestClient
        .post()
        .uri(BASE_PATH)
        .header(AvenirsSecurityHeaders.API_KEY, apiKey)
        .header(ADMIN_TOKEN_HEADER, adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange();
  }

  @Nested
  class WhenRecordingAnEvent {

    @Nested
    class AndTheCallerIsAnInternalServiceWithAValidToken {

      @Test
      void thenItShouldReturn201AndStoreTheEvent() {
        BddLogger.given("A valid statistic event with an emitter id");
        var eventId = UUID.randomUUID();

        BddLogger.when("POST /statistics/events is called with the api key and the admin token");
        var response = postWithApiKey(eventBody(eventId, "2026-11-20T10:00:00Z"));

        BddLogger.then("201 Created is returned and the event is stored");
        response
            .expectStatus()
            .isCreated()
            .expectBody()
            .jsonPath("$.createdItemId")
            .isEqualTo(eventId.toString());
        assertThat(statisticEventJpaRepository.findById(eventId)).isPresent();
      }

      @Test
      void thenItShouldStoreAnEventOnlyOnceForTheSameEventId() {
        BddLogger.given("An event already recorded with an emitter id");
        var eventId = UUID.randomUUID();
        postWithApiKey(eventBody(eventId, "2026-11-20T10:00:00Z")).expectStatus().isCreated();

        BddLogger.when("POST /statistics/events is called again with the same event");
        postWithApiKey(eventBody(eventId, "2026-11-20T10:00:00Z")).expectStatus().isCreated();

        BddLogger.then("A single event is stored");
        assertThat(statisticEventJpaRepository.count()).isEqualTo(1);
      }
    }

    @Nested
    class AndAMandatoryFieldIsMissing {

      @Test
      void thenItShouldReturn400AndStoreNothing() {
        BddLogger.given("An event without user");

        BddLogger.when("POST /statistics/events is called");
        var response =
            postWithApiKey(
                "{\"eventType\": \"TRACE_CREATED\", \"userCategory\": \"STUDENT\","
                    + " \"occurredAt\": \"2026-11-20T10:00:00Z\"}");

        BddLogger.then("400 Bad Request is returned with NOT_NULL and nothing is stored");
        response
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("NOT_NULL");
        assertThat(statisticEventJpaRepository.count()).isZero();
      }
    }

    @Nested
    class AndTheEventTypeIsUnknown {

      @Test
      void thenItShouldReturn400AndStoreNothing() {
        BddLogger.given("An event whose type is not in the list of known event types");

        BddLogger.when("POST /statistics/events is called");
        var response = postWithApiKey(eventBody(null, UNKNOWN_EVENT_TYPE, "2026-11-20T10:00:00Z"));

        BddLogger.then("400 Bad Request is returned and nothing is stored");
        response
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("MESSAGE_NOT_READABLE");
        assertThat(statisticEventJpaRepository.count()).isZero();
      }
    }

    @Nested
    class AndNoCredentialIsProvided {

      @Test
      void thenItShouldReturn401() {
        BddLogger.given("A valid statistic event without any credential");

        BddLogger.when("POST /statistics/events is called");
        var response =
            webTestClient
                .post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventBody(null, "2026-11-20T10:00:00Z"))
                .exchange();

        BddLogger.then("401 Unauthorized is returned");
        response.expectStatus().isUnauthorized();
      }
    }

    @Nested
    class AndTheUserLacksThePermission {

      @Test
      void thenItShouldReturn403() {
        BddLogger.given("A valid statistic event and a staff signed context");

        BddLogger.when("POST /statistics/events is called with the staff context");
        var response =
            webTestClient
                .post()
                .uri(BASE_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature)
                .header(ADMIN_TOKEN_HEADER, adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventBody(null, "2026-11-20T10:00:00Z"))
                .exchange();

        BddLogger.then("403 Forbidden is returned");
        response.expectStatus().isForbidden();
      }
    }

    @Nested
    class AndTheAdminTokenIsWrong {

      @Test
      void thenItShouldReturn403AndStoreNothing() {
        BddLogger.given("A valid statistic event and a wrong admin token");

        BddLogger.when("POST /statistics/events is called with the api key");
        var response =
            webTestClient
                .post()
                .uri(BASE_PATH)
                .header(AvenirsSecurityHeaders.API_KEY, apiKey)
                .header(ADMIN_TOKEN_HEADER, "wrong-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventBody(null, "2026-11-20T10:00:00Z"))
                .exchange();

        BddLogger.then("403 Forbidden is returned and nothing is stored");
        response.expectStatus().isForbidden();
        assertThat(statisticEventJpaRepository.count()).isZero();
      }
    }
  }

  @Nested
  class WhenCountingEvents {

    @Nested
    class AndTheCallerIsASuperAdmin {

      @Test
      void thenItShouldReturnTheCountOfThePeriod() {
        BddLogger.given("Events recorded inside and outside the requested period");
        postWithApiKey(eventBody(null, "2026-11-02T10:00:00Z")).expectStatus().isCreated();
        postWithApiKey(eventBody(null, "2026-11-10T10:00:00Z")).expectStatus().isCreated();
        postWithApiKey(eventBody(null, "2026-12-01T10:00:00Z")).expectStatus().isCreated();

        BddLogger.when("GET /statistics/events/count is called by a super admin");
        var response =
            webTestClient
                .get()
                .uri(
                    uriBuilder ->
                        uriBuilder
                            .path(COUNT_PATH)
                            .queryParam("type", "TRACE_CREATED")
                            .queryParam("institutionId", INSTITUTION_ID)
                            .queryParam("fromDate", "2026-11-01")
                            .queryParam("toDate", "2026-11-30")
                            .build())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, superAdminPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, superAdminSignature)
                .header(ADMIN_TOKEN_HEADER, adminToken)
                .exchange();

        BddLogger.then("200 OK is returned with the count of the period");
        response.expectStatus().isOk().expectBody().jsonPath("$.count").isEqualTo(2);
      }
    }

    @Nested
    class AndTheCallerIsNotASuperAdmin {

      @Test
      void thenItShouldReturn403() {
        BddLogger.given("A staff signed context");

        BddLogger.when("GET /statistics/events/count is called");
        var response =
            webTestClient
                .get()
                .uri(COUNT_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, staffPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, staffSignature)
                .header(ADMIN_TOKEN_HEADER, adminToken)
                .exchange();

        BddLogger.then("403 Forbidden is returned");
        response.expectStatus().isForbidden();
      }
    }

    @Nested
    class AndTheAdminTokenIsWrong {

      @Test
      void thenItShouldReturn403() {
        BddLogger.given("A super admin signed context and a wrong admin token");

        BddLogger.when("GET /statistics/events/count is called");
        var response =
            webTestClient
                .get()
                .uri(COUNT_PATH)
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, superAdminPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, superAdminSignature)
                .header(ADMIN_TOKEN_HEADER, "wrong-token")
                .exchange();

        BddLogger.then("403 Forbidden is returned");
        response.expectStatus().isForbidden();
      }
    }

    @Nested
    class AndThePeriodEndsBeforeItStarts {

      @Test
      void thenItShouldReturn400() {
        BddLogger.given("A period whose end is before its start");

        BddLogger.when("GET /statistics/events/count is called by a super admin");
        var response =
            webTestClient
                .get()
                .uri(
                    uriBuilder ->
                        uriBuilder
                            .path(COUNT_PATH)
                            .queryParam("fromDate", "2026-11-30")
                            .queryParam("toDate", "2026-11-01")
                            .build())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, superAdminPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, superAdminSignature)
                .header(ADMIN_TOKEN_HEADER, adminToken)
                .exchange();

        BddLogger.then("400 Bad Request is returned with END_DATE_BEFORE_START_DATE");
        response
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("END_DATE_BEFORE_START_DATE");
      }
    }

    @Nested
    class AndTheTypeIsUnknown {

      @Test
      void thenItShouldReturn400() {
        BddLogger.given("A type filter that is not in the list of known event types");

        BddLogger.when("GET /statistics/events/count is called by a super admin");
        var response =
            webTestClient
                .get()
                .uri(
                    uriBuilder ->
                        uriBuilder.path(COUNT_PATH).queryParam("type", UNKNOWN_EVENT_TYPE).build())
                .header(AvenirsSecurityHeaders.SIGNED_CONTEXT, superAdminPayload)
                .header(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, superAdminSignature)
                .header(ADMIN_TOKEN_HEADER, adminToken)
                .exchange();

        BddLogger.then("400 Bad Request is returned with INVALID_ARGUMENT_TYPE");
        response
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.code")
            .isEqualTo("INVALID_ARGUMENT_TYPE");
      }
    }
  }
}
