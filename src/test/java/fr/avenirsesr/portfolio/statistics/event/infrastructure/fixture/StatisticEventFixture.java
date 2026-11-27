package fr.avenirsesr.portfolio.statistics.event.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEvent;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventData;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class StatisticEventFixture {
  private UUID eventId;
  private EStatisticEventType eventType = EStatisticEventType.TRACE_CREATED;
  private UUID userId = UUID.randomUUID();
  private EUserCategory userCategory = EUserCategory.STUDENT;
  private Instant occurredAt = Instant.parse("2026-11-20T10:00:00Z");
  private UUID institutionId = UUID.randomUUID();
  private UUID programId = UUID.randomUUID();
  private String objectType = "TRACE";
  private UUID objectId = UUID.randomUUID();
  private Map<String, Object> metadata = Map.of("traceType", "FILE");

  public static StatisticEventFixture create() {
    return new StatisticEventFixture();
  }

  public StatisticEventFixture withEventId(UUID eventId) {
    this.eventId = eventId;
    return this;
  }

  public StatisticEventFixture withEventType(EStatisticEventType eventType) {
    this.eventType = eventType;
    return this;
  }

  public StatisticEventFixture withUserId(UUID userId) {
    this.userId = userId;
    return this;
  }

  public StatisticEventFixture withUserCategory(EUserCategory userCategory) {
    this.userCategory = userCategory;
    return this;
  }

  public StatisticEventFixture withOccurredAt(Instant occurredAt) {
    this.occurredAt = occurredAt;
    return this;
  }

  public StatisticEventFixture withInstitutionId(UUID institutionId) {
    this.institutionId = institutionId;
    return this;
  }

  public StatisticEventFixture withMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
    return this;
  }

  public StatisticEventData toData() {
    return new StatisticEventData(
        eventId,
        eventType,
        userId,
        userCategory,
        occurredAt,
        institutionId,
        programId,
        objectType,
        objectId,
        metadata);
  }

  public StatisticEvent toModel() {
    return StatisticEvent.create(toData());
  }
}
