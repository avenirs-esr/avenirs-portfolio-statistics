package fr.avenirsesr.portfolio.statistics.event.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import java.util.UUID;

public record StatisticEventCountCriteria(
    EStatisticEventType eventType, UUID institutionId, DateFilter dateFilter) {}
