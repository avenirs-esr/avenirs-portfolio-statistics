package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "statistic_event",
    indexes = {
      @Index(name = "idx_statistic_event_type_occurred_at", columnList = "event_type, occurred_at"),
      @Index(
          name = "idx_statistic_event_institution_occurred_at",
          columnList = "institution_id, occurred_at"),
      @Index(name = "idx_statistic_event_user_occurred_at", columnList = "user_id, occurred_at")
    })
@NoArgsConstructor
@Getter
@Setter
public class StatisticEventEntity extends AvenirsBaseEntity {

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false, length = 64)
  private EStatisticEventType eventType;

  @Column(name = "occurred_at", nullable = false)
  private Instant occurredAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_category", nullable = false, length = 32)
  private EUserCategory userCategory;

  @Column(name = "institution_id")
  private UUID institutionId;

  @Column(name = "program_id")
  private UUID programId;

  @Column(name = "object_type", length = 64)
  private String objectType;

  @Column(name = "object_id")
  private UUID objectId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata")
  private Map<String, Object> metadata;

  private StatisticEventEntity(
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
    this.setId(id);
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
    this.userId = userId;
    this.eventType = eventType;
    this.occurredAt = occurredAt;
    this.userCategory = userCategory;
    this.institutionId = institutionId;
    this.programId = programId;
    this.objectType = objectType;
    this.objectId = objectId;
    this.metadata = metadata;
  }

  public static StatisticEventEntity of(
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
    return new StatisticEventEntity(
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
