package fr.avenirsesr.portfolio.statistics.event.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.statistics.event.application.adapter.request.StatisticEventCreationRequest;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEvent;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventCountCriteria;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventData;
import fr.avenirsesr.portfolio.statistics.event.domain.port.input.StatisticEventService;
import fr.avenirsesr.portfolio.statistics.event.infrastructure.fixture.StatisticEventFixture;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StatisticEventControllerTest {

  private static final String ADMIN_TOKEN = "admin-token";
  private static final String WRONG_TOKEN = "wrong-token";

  @Mock private StatisticEventService statisticEventService;

  @InjectMocks private StatisticEventController controller;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(controller, "adminToken", ADMIN_TOKEN);
  }

  private static StatisticEventCreationRequest requestOf(StatisticEvent event) {
    return new StatisticEventCreationRequest(
        event.getId(),
        event.getEventType(),
        event.getUserId(),
        event.getUserCategory(),
        event.getOccurredAt(),
        event.getInstitutionId(),
        event.getProgramId(),
        event.getObjectType(),
        event.getObjectId(),
        event.getMetadata());
  }

  @Nested
  class WhenRecordingAnEvent {

    @Nested
    class AndTheAdminTokenIsValid {

      @Test
      void thenItShouldReturn201AndPassTheRequestToTheService() {
        BddLogger.given("A valid statistic event request and a valid admin token");
        var event = StatisticEventFixture.create().withEventId(UUID.randomUUID()).toModel();
        var request = requestOf(event);
        when(statisticEventService.record(any())).thenReturn(event);

        BddLogger.when("recordEvent is called");
        var response = controller.recordEvent(request, ADMIN_TOKEN);

        BddLogger.then("201 Created is returned and the request is passed through to the service");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().createdItemId()).isEqualTo(event.getId());
        var captor = ArgumentCaptor.forClass(StatisticEventData.class);
        verify(statisticEventService).record(captor.capture());
        assertThat(captor.getValue())
            .usingRecursiveComparison()
            .isEqualTo(
                new StatisticEventData(
                    request.eventId(),
                    request.eventType(),
                    request.userId(),
                    request.userCategory(),
                    request.occurredAt(),
                    request.institutionId(),
                    request.programId(),
                    request.objectType(),
                    request.objectId(),
                    request.metadata()));
      }
    }

    @Nested
    class AndTheAdminTokenIsWrong {

      @Test
      void thenItShouldReturn403AndRecordNothing() {
        BddLogger.given("A valid statistic event request and a wrong admin token");
        var request = requestOf(StatisticEventFixture.create().toModel());

        BddLogger.when("recordEvent is called");
        var response = controller.recordEvent(request, WRONG_TOKEN);

        BddLogger.then("403 Forbidden is returned and the service is not called");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verifyNoInteractions(statisticEventService);
      }
    }
  }

  @Nested
  class WhenCountingEvents {

    @Nested
    class AndTheAdminTokenIsValid {

      @Test
      void thenItShouldReturn200WithTheServiceCount() {
        BddLogger.given("Count filters on a type, an institution and a period");
        var institutionId = UUID.randomUUID();
        var fromDate = LocalDate.of(2026, 11, 1);
        var toDate = LocalDate.of(2026, 11, 30);
        var criteria =
            new StatisticEventCountCriteria(
                EStatisticEventType.TRACE_CREATED, institutionId, new DateFilter(fromDate, toDate));
        when(statisticEventService.count(criteria)).thenReturn(3L);

        BddLogger.when("countEvents is called");
        var response =
            controller.countEvents(
                EStatisticEventType.TRACE_CREATED, institutionId, fromDate, toDate, ADMIN_TOKEN);

        BddLogger.then("200 OK is returned with the count of the service");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().count()).isEqualTo(3L);
      }
    }

    @Nested
    class AndTheAdminTokenIsWrong {

      @Test
      void thenItShouldReturn403AndNotCallTheService() {
        BddLogger.given("A wrong admin token");

        BddLogger.when("countEvents is called");
        var response = controller.countEvents(null, null, null, null, WRONG_TOKEN);

        BddLogger.then("403 Forbidden is returned and the service is not called");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verifyNoInteractions(statisticEventService);
      }
    }

    @Nested
    class AndNoAdminTokenIsConfigured {

      @BeforeEach
      void setupGiven() {
        ReflectionTestUtils.setField(controller, "adminToken", "");
      }

      @Test
      void thenItShouldReturn503AndNotCallTheService() {
        BddLogger.given("A service without configured admin token");

        BddLogger.when("countEvents is called");
        var response = controller.countEvents(null, null, null, null, ADMIN_TOKEN);

        BddLogger.then("503 Service Unavailable is returned and the service is not called");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        verifyNoInteractions(statisticEventService);
      }
    }
  }
}
