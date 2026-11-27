package fr.avenirsesr.portfolio.statistics.event.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Event as sent by an emitting service.
 *
 * @param eventId optional identifier chosen by the emitter, used to make the recording idempotent
 * @param objectType optional kind of the target of the action (xAPI object), e.g. ACTIVITY
 * @param objectId optional identifier of the target of the action (xAPI object)
 */
public record StatisticEventData(
    UUID eventId,
    EStatisticEventType eventType,
    UUID userId,
    EUserCategory userCategory,
    Instant occurredAt,
    UUID institutionId,
    UUID programId,
    String objectType,
    UUID objectId,
    Map<String, Object> metadata) {}
