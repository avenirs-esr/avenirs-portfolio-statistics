package fr.avenirsesr.portfolio.statistics.event.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"count"})
public record StatisticEventCountDTO(long count) {}
