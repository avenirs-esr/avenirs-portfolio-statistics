package fr.avenirsesr.portfolio.statistics.event.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"createdItemId"})
public record CreationResponse(UUID createdItemId) {}
