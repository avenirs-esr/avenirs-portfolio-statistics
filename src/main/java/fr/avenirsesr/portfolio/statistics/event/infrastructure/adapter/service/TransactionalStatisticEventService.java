package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEvent;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventCountCriteria;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventData;
import fr.avenirsesr.portfolio.statistics.event.domain.port.input.StatisticEventService;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class TransactionalStatisticEventService implements StatisticEventService {
  private final StatisticEventService delegate;

  @Override
  @Transactional
  public StatisticEvent record(StatisticEventData data) {
    return delegate.record(data);
  }

  @Override
  @Transactional(readOnly = true)
  public long count(StatisticEventCountCriteria criteria) {
    return delegate.count(criteria);
  }
}
