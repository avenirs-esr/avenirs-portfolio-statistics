package fr.avenirsesr.portfolio.statistics.event.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class StatisticEvent extends AvenirsBaseModel {
  private final UUID userId;
  private final EStatisticEventType eventType;
  private final Instant occurredAt;
  private final EUserCategory userCategory;
  private final UUID institutionId;
  private final UUID programId;
  private final String objectType;
  private final UUID objectId;
  private final Map<String, Object> metadata;

  private StatisticEvent(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID userId,
      EStatisticEventType eventType,
      Instant occurredAt,
      EUserCategory userCategory,
      UUID institutionId,
      UUID programId,
      String objectType,
      UUID objectId,
      Map<String, Object> metadata) {
    super(id, createdAt, updatedAt);
    this.userId = userId;
    this.eventType = eventType;
    this.occurredAt = occurredAt;
    this.userCategory = userCategory;
    this.institutionId = institutionId;
    this.programId = programId;
    this.objectType = objectType;
    this.objectId = objectId;
    this.metadata = metadata == null ? null : Map.copyOf(metadata);
  }

  public static StatisticEvent create(StatisticEventData data) {
    Instant now = Instant.now();
    return new StatisticEvent(
        data.eventId() == null ? UUID.randomUUID() : data.eventId(),
        now,
        now,
        data.userId(),
        data.eventType(),
        data.occurredAt(),
        data.userCategory(),
        data.institutionId(),
        data.programId(),
        data.objectType(),
        data.objectId(),
        data.metadata());
  }

  public static StatisticEvent toDomain(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID userId,
      EStatisticEventType eventType,
      Instant occurredAt,
      EUserCategory userCategory,
      UUID institutionId,
      UUID programId,
      String objectType,
      UUID objectId,
      Map<String, Object> metadata) {
    return new StatisticEvent(
        id,
        createdAt,
        updatedAt,
        userId,
        eventType,
        occurredAt,
        userCategory,
        institutionId,
        programId,
        objectType,
        objectId,
        metadata);
  }
}
