package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventCountCriteria;
import fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.model.StatisticEventEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

/** Dates are interpreted in UTC; both bounds of the date filter are inclusive days. */
public final class StatisticEventSpecification {
  private static final String OCCURRED_AT = "occurredAt";

  private StatisticEventSpecification() {}

  public static Specification<StatisticEventEntity> matching(StatisticEventCountCriteria criteria) {
    DateFilter dateFilter = criteria.dateFilter();
    return withEventType(criteria.eventType())
        .and(withInstitutionId(criteria.institutionId()))
        .and(occurredFrom(dateFilter == null ? null : dateFilter.fromDate()))
        .and(occurredUntil(dateFilter == null ? null : dateFilter.toDate()));
  }

  public static Specification<StatisticEventEntity> withEventType(EStatisticEventType eventType) {
    if (eventType == null) {
      return Specification.unrestricted();
    }
    return (root, query, cb) -> cb.equal(root.get("eventType"), eventType);
  }

  public static Specification<StatisticEventEntity> withInstitutionId(UUID institutionId) {
    if (institutionId == null) {
      return Specification.unrestricted();
    }
    return (root, query, cb) -> cb.equal(root.get("institutionId"), institutionId);
  }

  public static Specification<StatisticEventEntity> occurredFrom(LocalDate fromDate) {
    if (fromDate == null) {
      return Specification.unrestricted();
    }
    Instant from = fromDate.atStartOfDay(ZoneOffset.UTC).toInstant();
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(OCCURRED_AT), from);
  }

  public static Specification<StatisticEventEntity> occurredUntil(LocalDate toDate) {
    if (toDate == null) {
      return Specification.unrestricted();
    }
    Instant until = toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    return (root, query, cb) -> cb.lessThan(root.get(OCCURRED_AT), until);
  }
}
