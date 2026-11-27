package fr.avenirsesr.portfolio.statistics.event.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import fr.avenirsesr.portfolio.statistics.event.application.adapter.dto.CreationResponse;
import fr.avenirsesr.portfolio.statistics.event.application.adapter.dto.StatisticEventCountDTO;
import fr.avenirsesr.portfolio.statistics.event.application.adapter.request.StatisticEventCreationRequest;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventCountCriteria;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventData;
import fr.avenirsesr.portfolio.statistics.event.domain.port.input.StatisticEventService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/statistics/events")
public class StatisticEventController {

  private final StatisticEventService statisticEventService;

  @Value("${admin.token:}")
  private String adminToken;

  @PreAuthorize("hasAuthority('statistic-event:record')")
  @PostMapping
  public ResponseEntity<CreationResponse> recordEvent(
      @Valid @RequestBody StatisticEventCreationRequest request,
      @RequestHeader(name = "X-ADMIN-TOKEN") String token) {
    HttpStatus rejection = checkAdminToken(token);
    if (rejection != null) {
      return ResponseEntity.status(rejection).build();
    }

    log.debug(
        "Received request to record statistic event [{}] for user [{}]",
        request.eventType(),
        request.userId());

    var recorded =
        statisticEventService.record(
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

    return ResponseEntity.status(HttpStatus.CREATED).body(new CreationResponse(recorded.getId()));
  }

  @PreAuthorize("hasAuthority('statistic-event:read')")
  @GetMapping("/count")
  public ResponseEntity<StatisticEventCountDTO> countEvents(
      @Parameter(
              in = ParameterIn.QUERY,
              schema = @Schema(ref = "#/components/schemas/EStatisticEventType"))
          @RequestParam(required = false)
          EStatisticEventType type,
      @RequestParam(required = false) UUID institutionId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate toDate,
      @RequestHeader(name = "X-ADMIN-TOKEN") String token) {
    HttpStatus rejection = checkAdminToken(token);
    if (rejection != null) {
      return ResponseEntity.status(rejection).build();
    }

    log.debug(
        "Received request to count statistic events (type={}, institutionId={}, fromDate={},"
            + " toDate={})",
        type,
        institutionId,
        fromDate,
        toDate);

    long count =
        statisticEventService.count(
            new StatisticEventCountCriteria(type, institutionId, new DateFilter(fromDate, toDate)));

    return ResponseEntity.ok(new StatisticEventCountDTO(count));
  }

  private HttpStatus checkAdminToken(String token) {
    if (adminToken == null || adminToken.isBlank()) {
      return HttpStatus.SERVICE_UNAVAILABLE;
    }
    if (!adminToken.equals(token)) {
      return HttpStatus.FORBIDDEN;
    }
    return null;
  }
}
