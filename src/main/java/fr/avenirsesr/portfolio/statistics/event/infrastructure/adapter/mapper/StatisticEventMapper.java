package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEvent;
import fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.model.StatisticEventEntity;

public class StatisticEventMapper implements Mapper<StatisticEventEntity, StatisticEvent> {

  public static final StatisticEventMapper INSTANCE = new StatisticEventMapper();

  @Override
  public StatisticEventEntity fromDomain(StatisticEvent event) {
    return StatisticEventEntity.of(
        event.getId(),
        event.getCreatedAt(),
        event.getUpdatedAt(),
        event.getUserId(),
        event.getEventType(),
        event.getOccurredAt(),
        event.getUserCategory(),
        event.getInstitutionId(),
        event.getProgramId(),
        event.getObjectType(),
        event.getObjectId(),
        event.getMetadata());
  }

  @Override
  public StatisticEvent toDomain(StatisticEventEntity entity) {
    return StatisticEvent.toDomain(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getUserId(),
        entity.getEventType(),
        entity.getOccurredAt(),
        entity.getUserCategory(),
        entity.getInstitutionId(),
        entity.getProgramId(),
        entity.getObjectType(),
        entity.getObjectId(),
        entity.getMetadata());
  }
}
