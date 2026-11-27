package fr.avenirsesr.portfolio.statistics.event.application.adapter.request;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Schema(requiredProperties = {"eventType", "userId", "userCategory", "occurredAt"})
public record StatisticEventCreationRequest(
    @Schema(description = "Optional emitter-side id making the recording idempotent") UUID eventId,
    @Schema(ref = "#/components/schemas/EStatisticEventType") EStatisticEventType eventType,
    UUID userId,
    @Schema(ref = "#/components/schemas/EUserCategory") EUserCategory userCategory,
    Instant occurredAt,
    UUID institutionId,
    UUID programId,
    @Schema(description = "Optional kind of the target of the action (xAPI object), e.g. ACTIVITY")
        String objectType,
    @Schema(description = "Optional identifier of the target of the action (xAPI object)")
        UUID objectId,
    @Schema(description = "Optional, limited complementary data") Map<String, Object> metadata) {}
